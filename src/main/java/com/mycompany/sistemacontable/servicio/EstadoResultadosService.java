package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.EstadoResultadosDAO;
import com.mycompany.sistemacontable.modelo.EstadoResultados;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.SaldoRolContable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class EstadoResultadosService {

    private final EstadoResultadosDAO estadoResultadosDAO;
    private final PeriodoService periodoService;
    private final KardexService kardexService;

    public EstadoResultadosService() {
        estadoResultadosDAO = new EstadoResultadosDAO();
        periodoService = new PeriodoService();
        kardexService = new KardexService();
    }

    public EstadoResultados generar() {

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        Map<String, SaldoRolContable> saldos =
                estadoResultadosDAO.obtenerSaldosPorRol(
                        periodo.getIdPeriodo()
                );

        // =====================================================
        // VENTAS
        // =====================================================

        BigDecimal ventas =
                obtenerAcreedor(
                        saldos,
                        "VENTAS"
                );

        BigDecimal devolucionVentas =
                obtenerDeudor(
                        saldos,
                        "DEVOLUCION_VENTAS"
                );

        BigDecimal descuentoVentas =
                estadoResultadosDAO.obtenerSaldoDeudorPorCodigo(
                        periodo.getIdPeriodo(),
                        "4.1.03"
                );

        BigDecimal ventasNetas =
                ventas
                        .subtract(devolucionVentas)
                        .subtract(descuentoVentas)
                        .setScale(2, RoundingMode.HALF_UP);


        // =====================================================
        // COMPRAS AJUSTADAS CON INVENTARIO
        // =====================================================
        // Regla solicitada para el proyecto:
        // Compras = Compras de la mayorizacion
        //         + Inventario inicial
        //         - Inventario final del Kardex PEPS
        // =====================================================

        // Solo compras de MERCADERÍA. Las compras de activos no forman
        // parte del costo de ventas aunque en datos históricos hayan quedado
        // registradas por error en la cuenta Compras.
        BigDecimal comprasBase =
                estadoResultadosDAO.obtenerComprasMercaderia(
                        periodo.getIdPeriodo()
                )
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal inventarioInicial =
                obtenerDeudor(
                        saldos,
                        "INVENTARIO"
                );

        // El inventario final debe venir del Kardex PEPS, no del saldo
        // contable del inventario inicial. El Kardex refleja las capas que
        // realmente quedan disponibles luego de compras, ventas y devoluciones.
        BigDecimal inventarioFinal =
                kardexService.obtenerSaldoPepsActualizado()
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal compras =
                comprasBase
                        .add(inventarioInicial)
                        .subtract(inventarioFinal)
                        .setScale(2, RoundingMode.HALF_UP);


        // =====================================================
        // COMPRAS TOTALES Y NETAS
        // =====================================================

        BigDecimal gastosSobreCompras =
                obtenerDeudor(
                        saldos,
                        "GASTOS_COMPRA"
                );

        BigDecimal comprasTotales =
                compras
                        .add(gastosSobreCompras)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal devolucionCompras =
                obtenerAcreedor(
                        saldos,
                        "DEVOLUCION_COMPRAS"
                );

        BigDecimal descuentoCompras =
                estadoResultadosDAO.obtenerSaldoAcreedorPorCodigo(
                        periodo.getIdPeriodo(),
                        "5.1.04"
                );

        BigDecimal comprasNetas =
                comprasTotales
                        .subtract(devolucionCompras)
                        .subtract(descuentoCompras)
                        .setScale(2, RoundingMode.HALF_UP);

        // En el formato solicitado, las compras netas bajan
        // directamente como Costo de Ventas.
        BigDecimal costoVentas =
                comprasNetas;


        // =====================================================
        // UTILIDAD BRUTA
        // =====================================================

        BigDecimal utilidadBruta =
                ventasNetas
                        .subtract(costoVentas)
                        .setScale(2, RoundingMode.HALF_UP);


        // =====================================================
        // GASTOS DE OPERACION
        // =====================================================

        BigDecimal gastosAdministrativos =
                obtenerDeudor(
                        saldos,
                        "GASTOS_ADMIN"
                );

        BigDecimal gastosVenta =
                obtenerDeudor(
                        saldos,
                        "GASTOS_VENTA"
                );

        BigDecimal gastosFinancieros =
                obtenerDeudor(
                        saldos,
                        "GASTOS_FINANCIEROS"
                );

        BigDecimal totalGastosOperativos =
                gastosAdministrativos
                        .add(gastosVenta)
                        .add(gastosFinancieros)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal utilidadOperacionalAntesImpuesto =
                utilidadBruta
                        .subtract(totalGastosOperativos)
                        .setScale(2, RoundingMode.HALF_UP);


        // =====================================================
        // OTROS PRODUCTOS
        // =====================================================

        BigDecimal otrosProductos =
                estadoResultadosDAO.obtenerSaldoAcreedorPorCodigo(
                        periodo.getIdPeriodo(),
                        "4.2.01"
                );

        BigDecimal utilidadEjercicio =
                utilidadOperacionalAntesImpuesto
                        .add(otrosProductos)
                        .setScale(2, RoundingMode.HALF_UP);


        // =====================================================
        // RESULTADO
        // =====================================================

        EstadoResultados resultado =
                new EstadoResultados();

        resultado.setVentas(ventas);
        resultado.setDevolucionVentas(devolucionVentas);
        resultado.setDescuentoVentas(descuentoVentas);
        resultado.setVentasNetas(ventasNetas);

        resultado.setInventarioInicial(inventarioInicial);
        resultado.setInventarioFinal(inventarioFinal);

        resultado.setComprasBase(comprasBase);
        resultado.setCompras(compras);
        resultado.setGastosSobreCompras(gastosSobreCompras);
        resultado.setComprasTotales(comprasTotales);
        resultado.setDevolucionCompras(devolucionCompras);
        resultado.setDescuentoCompras(descuentoCompras);
        resultado.setComprasNetas(comprasNetas);
        resultado.setCostoVentas(costoVentas);

        resultado.setUtilidadBruta(utilidadBruta);

        resultado.setGastosAdministrativos(gastosAdministrativos);
        resultado.setGastosVenta(gastosVenta);
        resultado.setGastosFinancieros(gastosFinancieros);
        resultado.setTotalGastosOperativos(totalGastosOperativos);
        resultado.setUtilidadOperacionalAntesImpuesto(
                utilidadOperacionalAntesImpuesto
        );

        resultado.setOtrosProductos(otrosProductos);
        resultado.setUtilidadEjercicio(utilidadEjercicio);

        return resultado;
    }

    private BigDecimal obtenerDeudor(
            Map<String, SaldoRolContable> saldos,
            String rol
    ) {

        SaldoRolContable saldo =
                saldos.get(rol);

        if (saldo == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valor =
                saldo.getTotalDebe()
                        .subtract(saldo.getTotalHaber());

        return positivoOZero(valor);
    }

    private BigDecimal obtenerAcreedor(
            Map<String, SaldoRolContable> saldos,
            String rol
    ) {

        SaldoRolContable saldo =
                saldos.get(rol);

        if (saldo == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valor =
                saldo.getTotalHaber()
                        .subtract(saldo.getTotalDebe());

        return positivoOZero(valor);
    }

    private BigDecimal positivoOZero(
            BigDecimal valor
    ) {

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
