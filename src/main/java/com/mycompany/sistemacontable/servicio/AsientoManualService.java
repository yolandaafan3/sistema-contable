package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.OperacionDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.Operacion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.modelo.ResultadoAsientoManual;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AsientoManualService {

    private final AsientoDAO asientoDAO;
    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final ProductoDAO productoDAO;
    private final PeriodoService periodoService;
    private final CalculoIVAService calculoIVAService;
    private final RecalculoKardexService recalculoKardexService;

    public AsientoManualService() {
        asientoDAO = new AsientoDAO();
        cuentaDAO = new CuentaDAO();
        operacionDAO = new OperacionDAO();
        productoDAO = new ProductoDAO();
        periodoService = new PeriodoService();
        calculoIVAService = new CalculoIVAService();
        recalculoKardexService = new RecalculoKardexService();
    }

    public ResultadoAsientoManual registrar(
            LocalDate fecha,
            String concepto,
            List<DetalleAsiento> detalles
    ) {
        return registrar(fecha, concepto, detalles, null, null, null, null, null);
    }

    public ResultadoAsientoManual registrar(
            LocalDate fecha,
            String concepto,
            List<DetalleAsiento> detalles,
            String tipoInventario,
            Integer idProducto,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            String formaPago
    ) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }

        periodoService.validarFecha(fecha);

        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException("El concepto del asiento es obligatorio.");
        }

        if (detalles == null || detalles.size() < 2) {
            throw new IllegalArgumentException("El asiento debe tener al menos dos líneas.");
        }

        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        for (DetalleAsiento detalle : detalles) {
            validarDetalle(detalle);

            BigDecimal debe = normalizarMonto(detalle.getDebe());
            BigDecimal haber = normalizarMonto(detalle.getHaber());

            detalle.setDebe(debe);
            detalle.setHaber(haber);

            totalDebe = totalDebe.add(debe);
            totalHaber = totalHaber.add(haber);
        }

        totalDebe = normalizarMonto(totalDebe);
        totalHaber = normalizarMonto(totalHaber);

        if (totalDebe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El asiento debe contener movimientos.");
        }

        if (totalDebe.compareTo(totalHaber) != 0) {
            BigDecimal diferencia = totalDebe.subtract(totalHaber).abs();
            throw new IllegalArgumentException(
                    "El asiento no está cuadrado. Debe: $" + totalDebe
                    + " | Haber: $" + totalHaber
                    + " | Diferencia: $" + diferencia
            );
        }

        boolean afectaInventario =
                "COMPRA".equals(tipoInventario) || "VENTA".equals(tipoInventario);

        if (afectaInventario) {
            validarDatosInventario(idProducto, cantidad, precioUnitario);
        }

        PeriodoContable periodo = periodoService.obtenerPeriodoActivo();

        if (periodo == null) {
            throw new IllegalStateException("No existe un periodo contable abierto.");
        }

        Connection conexion = null;

        try {
            conexion = Conexion.conectar();

            if (conexion == null) {
                throw new SQLException("No se pudo conectar con MySQL.");
            }

            conexion.setAutoCommit(false);

            Integer idOperacion = null;

            if (afectaInventario) {
                /*
                 * BLOQUEO TRANSACCIONAL DEL PRODUCTO.
                 *
                 * Una VENTA se valida ANTES de insertar la operación y el
                 * asiento. Así, una venta sin existencias nunca llega a existir
                 * en operaciones/asientos y no puede convertirse en una
                 * "operación fantasma" que bloquee compras posteriores.
                 *
                 * FOR UPDATE también evita sobreventa si dos usuarios intentan
                 * vender el mismo producto al mismo tiempo.
                 */
                Producto producto =
                        productoDAO.buscarPorIdParaActualizar(idProducto, conexion);

                if (producto == null || !producto.isActivo()) {
                    throw new IllegalArgumentException(
                            "El producto seleccionado no existe o está desactivado."
                    );
                }

                BigDecimal cantidadNormalizada =
                        cantidad.setScale(6, RoundingMode.HALF_UP);

                BigDecimal precioNormalizado =
                        precioUnitario.setScale(6, RoundingMode.HALF_UP);

                if ("VENTA".equals(tipoInventario)) {
                    BigDecimal existenciaDisponible =
                            producto.getExistenciaActual() == null
                                    ? BigDecimal.ZERO
                                    : producto.getExistenciaActual()
                                            .setScale(6, RoundingMode.HALF_UP);

                    if (cantidadNormalizada.compareTo(existenciaDisponible) > 0) {
                        throw new IllegalArgumentException(
                                "Inventario insuficiente. La venta solicita "
                                + cantidadNormalizada.stripTrailingZeros().toPlainString()
                                + " unidades, pero solo existen "
                                + existenciaDisponible.stripTrailingZeros().toPlainString()
                                + ". No se guardó ninguna operación ni asiento."
                        );
                    }
                }

                BigDecimal monto =
                        cantidadNormalizada.multiply(precioNormalizado)
                                .setScale(2, RoundingMode.HALF_UP);

                ResultadoIVA resultadoIVA =
                        "COMPRA".equals(tipoInventario)
                                ? calculoIVAService.calcularSobreBase(monto)
                                : calculoIVAService.calcular(monto);

                /*
                 * El asiento que el usuario revisó debe corresponder al mismo
                 * importe de la operación de inventario. Esto evita guardar
                 * un Kardex con valores distintos al Libro Diario.
                 */
                if (totalDebe.compareTo(resultadoIVA.getTotal()) != 0) {
                    throw new IllegalArgumentException(
                            "El total del asiento ($" + totalDebe
                            + ") no coincide con el total calculado para "
                            + cantidadNormalizada.stripTrailingZeros().toPlainString()
                            + " unidades a $" + precioNormalizado.toPlainString()
                            + " ($" + resultadoIVA.getTotal() + "). "
                            + "Vuelve a usar 'Sugerir cuentas' o corrige los datos."
                    );
                }

                Operacion operacion = new Operacion();
                operacion.setIdPeriodo(periodo.getIdPeriodo());
                operacion.setFecha(fecha);
                operacion.setTipoOperacion(tipoInventario);
                operacion.setConcepto(concepto.trim());
                operacion.setIdProducto(idProducto);
                operacion.setCantidad(cantidadNormalizada);
                operacion.setPrecioUnitario(precioNormalizado);
                operacion.setSubtotal(resultadoIVA.getSubtotal());
                operacion.setIva(resultadoIVA.getIva());
                operacion.setTotal(resultadoIVA.getTotal());
                operacion.setFormaPago(
                        formaPago == null || formaPago.isBlank()
                                ? "EFECTIVO"
                                : formaPago
                );

                idOperacion = operacionDAO.insertar(operacion, conexion);

                if ("COMPRA".equals(tipoInventario)) {
                    /*
                     * costo_compra queda como último costo neto de referencia.
                     * Las capas históricas PEPS no se modifican: cada operación
                     * conserva su costo real en operaciones/kardex.
                     */
                    BigDecimal ultimoCostoNeto =
                            resultadoIVA.getSubtotal()
                                    .divide(
                                            cantidadNormalizada,
                                            2,
                                            RoundingMode.HALF_UP
                                    );

                    productoDAO.actualizarCostoCompra(
                            idProducto,
                            ultimoCostoNeto,
                            conexion
                    );
                }
            }

            int numeroAsiento =
                    asientoDAO.obtenerSiguienteNumero(
                            periodo.getIdPeriodo(),
                            conexion
                    );

            AsientoContable asiento = new AsientoContable();
            asiento.setIdPeriodo(periodo.getIdPeriodo());
            asiento.setIdOperacion(idOperacion);
            asiento.setNumeroAsiento(numeroAsiento);
            asiento.setFecha(fecha);
            asiento.setConcepto(concepto.trim());
            asiento.setTipoAsiento(afectaInventario ? "AUTOMATICO" : "MANUAL");
            asiento.setEstado("CONTABILIZADO");

            int idAsiento =
                    asientoDAO.insertarAsiento(asiento, conexion);

            for (DetalleAsiento detalle : detalles) {
                detalle.setIdAsiento(idAsiento);
                asientoDAO.insertarDetalle(detalle, conexion);
            }

            if (afectaInventario) {
                /*
                 * El recálculo se ejecuta antes del commit y en la misma
                 * conexión. Si falla PEPS/Kardex, también se revierte el
                 * asiento y la operación; nunca quedan desincronizados.
                 */
                recalculoKardexService.recalcularProducto(
                        idProducto,
                        conexion
                );
            }

            conexion.commit();

            return new ResultadoAsientoManual(
                    idAsiento,
                    numeroAsiento,
                    totalDebe,
                    totalHaber
            );

        } catch (Exception e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al hacer rollback: " + ex.getMessage());
                }
            }

            throw new RuntimeException(
                    "No se pudo registrar el asiento: " + e.getMessage(),
                    e
            );

        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    private void validarDatosInventario(
            Integer idProducto,
            BigDecimal cantidad,
            BigDecimal precioUnitario
    ) {
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar el producto que afecta el Kardex."
            );
        }

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de unidades debe ser mayor que cero."
            );
        }

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio unitario debe ser mayor que cero."
            );
        }
    }

    private void validarDetalle(DetalleAsiento detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("Existe una línea de asiento vacía.");
        }

        Cuenta cuenta = cuentaDAO.buscarPorId(detalle.getIdCuenta());

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "Una de las cuentas seleccionadas no existe."
            );
        }

        if (!cuenta.isActivo()) {
            throw new IllegalArgumentException(
                    "La cuenta " + cuenta.getCodigo() + " - "
                    + cuenta.getNombre() + " está desactivada."
            );
        }

        if (!cuenta.isPermiteMovimiento()) {
            throw new IllegalArgumentException(
                    "La cuenta " + cuenta.getCodigo() + " - "
                    + cuenta.getNombre() + " no permite movimientos directos."
            );
        }

        BigDecimal debe = normalizarMonto(detalle.getDebe());
        BigDecimal haber = normalizarMonto(detalle.getHaber());

        if (debe.compareTo(BigDecimal.ZERO) < 0
                || haber.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Los valores Debe y Haber no pueden ser negativos."
            );
        }

        if (debe.compareTo(BigDecimal.ZERO) > 0
                && haber.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException(
                    "Una línea no puede contener valor en Debe y Haber al mismo tiempo."
            );
        }

        if (debe.compareTo(BigDecimal.ZERO) == 0
                && haber.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException(
                    "Cada línea debe contener un valor en Debe o Haber."
            );
        }
    }

    private BigDecimal normalizarMonto(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
