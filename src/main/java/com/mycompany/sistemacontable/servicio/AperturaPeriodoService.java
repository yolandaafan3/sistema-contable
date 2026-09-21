package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.KardexDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AperturaPeriodoService {
    private final PeriodoService periodoService = new PeriodoService();
    private final AsientoDAO asientoDAO = new AsientoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final KardexDAO kardexDAO = new KardexDAO();

    public BigDecimal obtenerValorInventarioInicialConfigurado() {
        return productoDAO.obtenerValorInventarioInicialTotal().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Compatibilidad con llamadas antiguas. Si no hay inventario, funciona igual.
     * Para inventario mayor que cero se debe usar la sobrecarga que recibe producto y costo inicial.
     */
    public String registrarApertura(LocalDate fecha, BigDecimal efectivoInicial, BigDecimal valorInventarioMostrado) {
        return registrarApertura(fecha, efectivoInicial, valorInventarioMostrado, null, null);
    }

    public String registrarApertura(
            LocalDate fecha,
            BigDecimal efectivoInicial,
            BigDecimal valorInventarioMostrado,
            Integer idProductoInventario,
            BigDecimal costoUnitarioInicial
    ) {
        if (fecha == null) throw new IllegalArgumentException("Debe seleccionar la fecha de apertura.");
        if (efectivoInicial == null || efectivoInicial.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El efectivo inicial no puede ser negativo.");

        periodoService.validarFecha(fecha);
        PeriodoContable periodo = periodoService.obtenerPeriodoActivo();
        if (periodo == null) throw new IllegalStateException("No existe un período contable activo.");
        if (!fecha.equals(periodo.getFechaInicio()))
            throw new IllegalArgumentException("La apertura debe registrarse en la fecha de inicio del período: " + periodo.getFechaInicio() + ".");

        BigDecimal efectivo = efectivoInicial.setScale(2, RoundingMode.HALF_UP);
        BigDecimal inventario = valorInventarioMostrado == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : valorInventarioMostrado.setScale(2, RoundingMode.HALF_UP);

        if (inventario.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El inventario inicial no puede ser negativo.");
        if (efectivo.signum() == 0 && inventario.signum() == 0)
            throw new IllegalArgumentException("La apertura debe contener efectivo y/o inventario inicial mayor que cero.");

        BigDecimal costoInicial = costoUnitarioInicial == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : costoUnitarioInicial.setScale(2, RoundingMode.HALF_UP);
        BigDecimal cantidadInicial = BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        BigDecimal costoPepsInicial = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        if (inventario.signum() > 0) {
            if (idProductoInventario == null || idProductoInventario <= 0)
                throw new IllegalArgumentException("Selecciona el producto al que pertenece el inventario inicial.");
            if (costoInicial.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("El costo unitario inicial debe ser mayor que cero.");
            cantidadInicial = inventario.divide(costoInicial, 0, RoundingMode.HALF_UP);
            if (cantidadInicial.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("La cantidad inicial calculada debe ser mayor que cero.");

            // El Kardex trabaja con unidades físicas enteras y conserva el costo
            // unitario visible ingresado por el usuario. Por eso, si el redondeo de
            // unidades produce una pequeña diferencia, el saldo PEPS refleja
            // cantidad x costo unitario (ej.: 678 x 8.85 = 6000.30).
            costoPepsInicial = costoInicial.setScale(6, RoundingMode.HALF_UP);
        }

        Connection c = null;
        try {
            c = Conexion.conectar();
            if (c == null) throw new SQLException("No se pudo conectar con la base de datos.");
            c.setAutoCommit(false);
            validarPeriodoSinMovimientos(periodo.getIdPeriodo(), c);

            int idCaja = buscarCuenta(c, "1.1.01.01", "Caja");
            int idInventario = buscarCuenta(c, "1.1.03", "Inventario");
            int idCapital = buscarCuenta(c, "3.1.01", "Capital Social");
            BigDecimal capital = efectivo.add(inventario).setScale(2, RoundingMode.HALF_UP);

            int numero = asientoDAO.obtenerSiguienteNumero(periodo.getIdPeriodo(), c);
            AsientoContable a = new AsientoContable();
            a.setIdPeriodo(periodo.getIdPeriodo()); a.setIdOperacion(null); a.setNumeroAsiento(numero);
            a.setFecha(fecha); a.setConcepto("Apertura del período contable");
            a.setTipoAsiento("AUTOMATICO"); a.setEstado("CONTABILIZADO");
            int idAsiento = asientoDAO.insertarAsiento(a,c);

            if (efectivo.signum() > 0) insertarDetalle(idAsiento,idCaja,"Efectivo inicial",efectivo,BigDecimal.ZERO,c);
            if (inventario.signum() > 0) insertarDetalle(idAsiento,idInventario,"Inventario inicial",inventario,BigDecimal.ZERO,c);
            insertarDetalle(idAsiento,idCapital,"Capital Social",BigDecimal.ZERO,capital,c);

            String detalleInventario = "";
            if (inventario.signum() > 0) {
                Producto producto = productoDAO.buscarPorId(idProductoInventario, c);
                if (producto == null || !producto.isActivo()) {
                    throw new SQLException("No se encontró el producto seleccionado para el inventario inicial.");
                }

                actualizarDatosInventarioInicial(
                        producto.getIdProducto(), costoPepsInicial, inventario, cantidadInicial, c
                );

                BigDecimal valorKardexInicial = cantidadInicial.multiply(costoPepsInicial)
                        .setScale(2, RoundingMode.HALF_UP);

                MovimientoKardex movimiento = new MovimientoKardex();
                movimiento.setIdProducto(producto.getIdProducto());
                movimiento.setIdAsiento(idAsiento);
                movimiento.setFecha(fecha);
                movimiento.setConcepto("Inventario inicial");
                movimiento.setUnidadesEntrada(cantidadInicial);
                movimiento.setUnidadesSalida(BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP));
                movimiento.setUnidadesExistencia(cantidadInicial);
                movimiento.setCostoUnitario(costoPepsInicial);
                movimiento.setCostoPeps(costoPepsInicial);
                movimiento.setSaldoDeudor(valorKardexInicial);
                movimiento.setSaldoAcreedor(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                movimiento.setSaldo(valorKardexInicial);

                int idKardex = kardexDAO.insertarMovimiento(movimiento, c);
                kardexDAO.insertarCapaPeps(
                        producto.getIdProducto(), idKardex, fecha, cantidadInicial, costoPepsInicial, c
                );
                asegurarTablaAperturaLotes(c);
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO inventario_apertura_lotes(id_periodo,id_producto,fecha_origen,cantidad,costo_unitario,orden_lote) VALUES(?,?,?,?,?,1)")) {
                    ps.setInt(1, periodo.getIdPeriodo());
                    ps.setInt(2, producto.getIdProducto());
                    ps.setDate(3, java.sql.Date.valueOf(fecha));
                    ps.setBigDecimal(4, cantidadInicial);
                    ps.setBigDecimal(5, costoPepsInicial);
                    ps.executeUpdate();
                }

                detalleInventario = "\nProducto: " + producto.getCodigo() + " - " + producto.getNombre()
                        + "\nCosto unitario inicial: $" + costoInicial.setScale(2, RoundingMode.HALF_UP)
                        + "\nUnidades iniciales: " + cantidadInicial.toPlainString();
            }

            c.commit();

            return """
                   Apertura registrada correctamente.

                   Asiento N.º %d
                   Caja: $%,.2f
                   Inventario inicial: $%,.2f
                   Capital Social: $%,.2f
                   %s

                   Capital Social = Caja + Inventario inicial.
                   """.formatted(numero,efectivo,inventario,capital,detalleInventario);
        } catch(Exception e) {
            if(c!=null) try{c.rollback();}catch(SQLException ignored){}
            throw new RuntimeException(mensaje(e),e);
        } finally {
            if(c!=null) try{c.setAutoCommit(true);c.close();}catch(SQLException ignored){}
        }
    }

    private void actualizarDatosInventarioInicial(
            int idProducto,
            BigDecimal costoInicial,
            BigDecimal valorInicial,
            BigDecimal existenciaInicial,
            Connection c
    ) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
                UPDATE productos
                   SET costo_inicial=?, valor_inventario_inicial=?,
                       existencia_inicial=?, existencia_actual=?
                 WHERE id_producto=? AND activo=TRUE
                """)) {
            ps.setBigDecimal(1, costoInicial);
            ps.setBigDecimal(2, valorInicial);
            ps.setBigDecimal(3, existenciaInicial);
            ps.setBigDecimal(4, existenciaInicial);
            ps.setInt(5, idProducto);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("No se pudo actualizar el inventario inicial del producto.");
            }
        }
    }

    private void validarPeriodoSinMovimientos(int idPeriodo, Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) cantidad FROM asientos_contables WHERE id_periodo=?")){
            ps.setInt(1,idPeriodo); try(ResultSet rs=ps.executeQuery()){
                if(rs.next() && rs.getInt("cantidad")>0) throw new IllegalStateException("El período ya contiene asientos contables. No se puede registrar nuevamente la apertura.");
            }
        }
    }
    private int buscarCuenta(Connection c,String codigo,String nombre)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT id_cuenta FROM catalogo_cuentas WHERE codigo=? AND activo=TRUE AND permite_movimiento=TRUE LIMIT 1")){
            ps.setString(1,codigo); try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return rs.getInt(1); }
        }
        throw new SQLException("No se encontró la cuenta "+nombre+" ("+codigo+").");
    }
    private void insertarDetalle(int idAsiento,int idCuenta,String desc,BigDecimal debe,BigDecimal haber,Connection c)throws SQLException{
        DetalleAsiento d=new DetalleAsiento(); d.setIdAsiento(idAsiento); d.setIdCuenta(idCuenta); d.setDescripcion(desc);
        d.setDebe(debe.setScale(2,RoundingMode.HALF_UP)); d.setHaber(haber.setScale(2,RoundingMode.HALF_UP)); asientoDAO.insertarDetalle(d,c);
    }
    private String mensaje(Throwable e){ String m="Ocurrió un error al registrar la apertura."; for(Throwable x=e;x!=null;x=x.getCause()) if(x.getMessage()!=null&&!x.getMessage().isBlank())m=x.getMessage(); return m; }
    private void asegurarTablaAperturaLotes(Connection c) throws SQLException {
        try (java.sql.Statement st = c.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS inventario_apertura_lotes (
                    id_apertura_lote BIGINT AUTO_INCREMENT PRIMARY KEY,
                    id_periodo INT NOT NULL,
                    id_producto INT NOT NULL,
                    fecha_origen DATE NOT NULL,
                    cantidad DECIMAL(18,6) NOT NULL,
                    costo_unitario DECIMAL(18,6) NOT NULL,
                    orden_lote INT NOT NULL,
                    INDEX idx_apertura_lotes_periodo_producto(id_periodo,id_producto,orden_lote),
                    CONSTRAINT fk_apertura_lotes_periodo FOREIGN KEY(id_periodo) REFERENCES periodos_contables(id_periodo) ON DELETE CASCADE,
                    CONSTRAINT fk_apertura_lotes_producto FOREIGN KEY(id_producto) REFERENCES productos(id_producto)
                ) ENGINE=InnoDB
                """);
        }
    }

}
