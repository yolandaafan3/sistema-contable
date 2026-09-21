package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SugerenciaAsientoService {

    public static final String COMPRA_INVENTARIO = "COMPRA";
    public static final String VENTA_INVENTARIO = "VENTA";
    public static final String COMPRA_ACTIVO = "COMPRA_ACTIVO";
    public static final String GASTO = "GASTO";
    public static final String DEVOLUCION_COMPRA = "DEVOLUCION_COMPRA";
    public static final String DEVOLUCION_VENTA = "DEVOLUCION_VENTA";
    public static final String NORMAL = "NORMAL";

    private final CuentaDAO cuentaDAO;
    private final CalculoIVAService calculoIVAService;

    public SugerenciaAsientoService() {
        cuentaDAO = new CuentaDAO();
        calculoIVAService = new CalculoIVAService();
    }

    public String detectarTipo(String descripcion) {
        String texto = normalizar(descripcion);

        // Las devoluciones se detectan ANTES que las palabras compra/venta.
        // De lo contrario "devolución sobre venta" se interpretaba como una venta
        // normal y salía inventario en lugar de regresar al Kardex.
        if (contiene(texto, "devolucion sobre venta", "devolucion de venta", "devolucion venta",
                "cliente devuelve", "cliente nos devuelve")) {
            return DEVOLUCION_VENTA;
        }

        if (contiene(texto, "devolucion sobre compra", "devolucion de compra", "devolucion compra",
                "devolvemos al proveedor", "devolver al proveedor")) {
            return DEVOLUCION_COMPRA;
        }

        if (contiene(texto, "venta", "vendemos", "vendio", "se venden", "se vende")) {
            return VENTA_INVENTARIO;
        }

        if (contiene(texto, "chequera", "comision bancaria", "comision del banco")) {
            return GASTO;
        }

        if (contiene(texto,
                "vehiculo", "vehículo", "automovil", "automóvil", "carro",
                "computadora", "ordenador", "laptop", "impresora",
                "mobiliario", "escritorio", "silla de oficina",
                "equipo de computo", "equipo informatico",
                "para la oficina", "para uso de la empresa", "para uso propio")) {
            return COMPRA_ACTIVO;
        }

        if (contiene(texto, "compra", "compramos", "adquisicion", "se compran", "se compra")) {
            if (contiene(texto,
                    "mercaderia", "mercancia", "inventario",
                    "para vender", "para la venta", "destinado a la venta",
                    "destinados a la venta", "producto para vender",
                    "productos para vender")) {
                return COMPRA_INVENTARIO;
            }

            /*
             * Una compra ambigua NO se manda automáticamente al Kardex.
             * La interfaz preguntará al usuario si se trata de mercadería
             * para la venta o de una compra para uso de la empresa.
             */
            return NORMAL;
        }

        return NORMAL;
    }

    public BigDecimal extraerCantidadInventario(String descripcion) {
        if (descripcion == null) {
            return null;
        }

        String texto = normalizar(descripcion);

        Pattern[] patrones = new Pattern[]{
            Pattern.compile("(?:compra|compramos|compran|compraron|venta|vendemos|venden|vendieron|se compran|se venden)\\s+(?:de\\s+)?(-?\\d+(?:[\\.,]\\d+)?)\\s*(?:unidades|unidad|uds|ud|u\\b)"),
            Pattern.compile("(-?\\d+(?:[\\.,]\\d+)?)\\s*(?:unidades|unidad|uds|ud|u\\b)"),
            Pattern.compile("(?:compra|venta)\\s+de\\s+(-?\\d+(?:[\\.,]\\d+)?)\\b")
        };

        for (Pattern patron : patrones) {
            Matcher matcher = patron.matcher(texto);
            if (matcher.find()) {
                return parsearNumero(matcher.group(1), 6);
            }
        }

        return null;
    }

    public BigDecimal extraerPrecioUnitario(String descripcion) {
        if (descripcion == null) {
            return null;
        }

        String texto = normalizar(descripcion);

        Pattern[] patrones = new Pattern[]{
            Pattern.compile("(?:a|por)\\s*\\$?\\s*(-?\\d+(?:[\\.,]\\d{1,6})?)\\s*(?:c/u|cada una|cada uno|cada|por unidad|la unidad)"),
            Pattern.compile("\\$\\s*(-?\\d+(?:[\\.,]\\d{1,6})?)\\s*(?:c/u|cada una|cada uno|cada|por unidad|la unidad)"),
            Pattern.compile("(?:precio|costo)(?:\\s+unitario)?\\s*(?:de|:)?\\s*\\$?\\s*(-?\\d+(?:[\\.,]\\d{1,6})?)")
        };

        for (Pattern patron : patrones) {
            Matcher matcher = patron.matcher(texto);
            if (matcher.find()) {
                return parsearNumero(matcher.group(1), 6);
            }
        }

        return null;
    }

    public BigDecimal extraerMontoTotalInventario(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return null;
        }

        String texto = normalizar(descripcion);

        Pattern[] patrones = new Pattern[]{
            Pattern.compile("(?:por\\s+(?:un\\s+)?total\\s+de|total\\s+de|total\\s*:?)\\s*\\$?\\s*(-?\\d+(?:[\\.,]\\d{1,2})?)"),
            Pattern.compile("(?:mercaderia|mercancia|inventario|productos?|unidades?)\\s+por\\s*\\$\\s*(-?\\d+(?:[\\.,]\\d{1,2})?)"),
            Pattern.compile("\\bpor\\s*\\$\\s*(-?\\d+(?:[\\.,]\\d{1,2})?)(?!\\s*(?:c/u|cada|por unidad))"),
            Pattern.compile("(?:compra|venta|compramos|vendemos)(?:\\s+de\\s+[a-z0-9áéíóúñ\\s-]+)?\\s+\\$?\\s*(-?\\d+(?:[\\.,]\\d{1,2})?)\\s*$")
        };

        for (Pattern patron : patrones) {
            Matcher matcher = patron.matcher(texto);
            if (matcher.find()) {
                return parsearNumero(matcher.group(1), 2);
            }
        }

        return null;
    }

    public String detectarFormaPago(String descripcion) {
        String texto = normalizar(descripcion);

        boolean credito = contiene(texto, "credito", "al credito", "a credito", "plazo");
        boolean banco = contiene(texto, "banco", "transferencia", "cheque", "cuenta bancaria");
        boolean efectivo = contiene(texto, "efectivo", "contado", "caja");

        if (credito && (banco || efectivo)) {
            return "MIXTO";
        }

        if (credito) {
            return "CREDITO";
        }

        if (banco) {
            return "BANCO";
        }

        return "EFECTIVO";
    }

    public BigDecimal extraerPorcentajePagoInmediato(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return null;
        }

        String texto = normalizar(descripcion);
        Matcher m = Pattern.compile(
                "(?:cancela|paga|pagando|abona|abono)?\\s*(?:el\\s*)?(\\d{1,3}(?:[\\.,]\\d+)?)\\s*%"
        ).matcher(texto);

        if (!m.find()) {
            return null;
        }

        BigDecimal porcentaje = parsearNumero(m.group(1), 2);

        if (porcentaje.compareTo(BigDecimal.ZERO) < 0
                || porcentaje.compareTo(new BigDecimal("100")) > 0) {
            return null;
        }

        return porcentaje;
    }

    public List<DetalleAsiento> sugerirInventario(
            String descripcion,
            String tipo,
            BigDecimal cantidad,
            BigDecimal precioUnitario
    ) throws Exception {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero.");
        }

        BigDecimal monto =
                cantidad.multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);

        String texto = normalizar(descripcion);

        if (COMPRA_INVENTARIO.equals(tipo)) {
            return sugerirCompra(texto, monto);
        }

        if (VENTA_INVENTARIO.equals(tipo)) {
            return sugerirVenta(texto, monto);
        }

        throw new IllegalArgumentException("El tipo indicado no corresponde a inventario.");
    }

    public List<DetalleAsiento> sugerir(String descripcion) throws Exception {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe escribir la descripción de la operación.");
        }

        String texto = normalizar(descripcion);
        String tipo = detectarTipo(descripcion);

        if (COMPRA_INVENTARIO.equals(tipo) || VENTA_INVENTARIO.equals(tipo)) {
            BigDecimal cantidad = extraerCantidadInventario(descripcion);
            BigDecimal precio = extraerPrecioUnitario(descripcion);
            BigDecimal montoTotal = extraerMontoTotalInventario(descripcion);

            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "La operación afecta inventario y necesita la cantidad de unidades para Kardex."
                );
            }

            if ((precio == null || precio.compareTo(BigDecimal.ZERO) <= 0)
                    && montoTotal != null
                    && montoTotal.compareTo(BigDecimal.ZERO) > 0) {
                precio = montoTotal.divide(cantidad, 6, RoundingMode.HALF_UP);
            }

            if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "La operación afecta inventario y necesita el monto total o el precio unitario."
                );
            }

            return sugerirInventario(descripcion, tipo, cantidad, precio);
        }

        BigDecimal monto = extraerMontoGeneral(descripcion);

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("No se pudo identificar el monto de la operación.");
        }

        if (COMPRA_ACTIVO.equals(tipo)) {
            return sugerirCompraActivo(texto, monto);
        }

        if (GASTO.equals(tipo) || contiene(texto, "alquiler", "papeleria", "servicio", "internet",
                "energia", "agua", "telefono", "comision", "interes", "chequera")) {
            return sugerirGasto(texto, monto);
        }

        if (contiene(texto, "compra", "compramos", "adquisicion", "se compra", "se compran")) {
            throw new IllegalArgumentException(
                    "La compra no indica si corresponde a mercadería para la venta o a uso de la empresa."
            );
        }

        if (contiene(texto, "cobro", "cobramos", "cliente paga", "cliente cancela")) {
            return sugerirCobro(texto, monto);
        }

        if (contiene(texto, "pago proveedor", "pagamos proveedor", "cancelamos proveedor")) {
            return sugerirPagoProveedor(texto, monto);
        }

        throw new IllegalArgumentException(
                "No se pudo identificar automáticamente el tipo de operación. "
                + "Puede registrar el asiento manualmente."
        );
    }

    private List<DetalleAsiento> sugerirCompra(String texto, BigDecimal monto) throws Exception {
        // En compras de mercadería el costo unitario es costo neto de la
        // mercancía. El IVA se calcula encima de esa base y no forma parte
        // del costo PEPS.
        ResultadoIVA r = calculoIVAService.calcularSobreBase(monto);
        List<DetalleAsiento> detalles = new ArrayList<>();

        agregarDebe(detalles, buscarPorRol("COMPRAS"), r.getSubtotal());

        if (r.getIva().compareTo(BigDecimal.ZERO) > 0) {
            agregarDebe(detalles, buscarPorRol("IVA_CREDITO"), r.getIva());
        }

        if ("CREDITO".equals(detectarFormaPago(texto))) {
            String codigoCuentaCredito = contiene(texto, "acreedores varios", "acreedor varios", "acreedores")
                    ? "2.1.01.02"
                    : "2.1.01.01";
            agregarHaber(detalles, buscarPorCodigo(codigoCuentaCredito), r.getTotal());
        } else {
            agregarHaber(detalles, buscarCuentaEfectivo(texto), r.getTotal());
        }

        return detalles;
    }

    public List<DetalleAsiento> sugerirCompraInventario(
            BigDecimal cantidad,
            BigDecimal costoUnitario,
            DistribucionPago distribucion,
            String cuentaCredito
    ) throws Exception {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (costoUnitario == null || costoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El costo unitario debe ser mayor que cero.");
        }

        if (distribucion == null) {
            throw new IllegalArgumentException("Debe indicar cómo se pagará la compra.");
        }

        BigDecimal subtotal = cantidad
                .multiply(costoUnitario)
                .setScale(2, RoundingMode.HALF_UP);

        ResultadoIVA r = calculoIVAService.calcularSobreBase(subtotal);

        if (distribucion.getTotal().compareTo(r.getTotal()) != 0) {
            throw new IllegalArgumentException(
                    "La distribución del pago ($" + distribucion.getTotal()
                    + ") no coincide con el total de la compra ($"
                    + r.getTotal() + ")."
            );
        }

        List<DetalleAsiento> detalles = new ArrayList<>();

        agregarDebe(detalles, buscarPorRol("COMPRAS"), r.getSubtotal());

        if (r.getIva().compareTo(BigDecimal.ZERO) > 0) {
            agregarDebe(detalles, buscarPorRol("IVA_CREDITO"), r.getIva());
        }

        if (distribucion.getEfectivo().compareTo(BigDecimal.ZERO) > 0) {
            agregarHaber(
                    detalles,
                    buscarPorCodigo("1.1.01.01"),
                    distribucion.getEfectivo()
            );
        }

        if (distribucion.getBanco().compareTo(BigDecimal.ZERO) > 0) {
            agregarHaber(
                    detalles,
                    buscarPorCodigo("1.1.01.02"),
                    distribucion.getBanco()
            );
        }

        if (distribucion.getCredito().compareTo(BigDecimal.ZERO) > 0) {
            String codigoCuentaCredito =
                    "ACREEDORES VARIOS".equalsIgnoreCase(cuentaCredito)
                            ? "2.1.01.02"
                            : "2.1.01.01";

            agregarHaber(
                    detalles,
                    buscarPorCodigo(codigoCuentaCredito),
                    distribucion.getCredito()
            );
        }

        return detalles;
    }

    private List<DetalleAsiento> sugerirVenta(String texto, BigDecimal monto) throws Exception {
        ResultadoIVA r = calculoIVAService.calcular(monto);
        List<DetalleAsiento> detalles = new ArrayList<>();

        if ("CREDITO".equals(detectarFormaPago(texto))) {
            agregarDebe(detalles, buscarPorCodigo("1.1.02.01"), r.getTotal());
        } else {
            agregarDebe(detalles, buscarCuentaEfectivo(texto), r.getTotal());
        }

        agregarHaber(detalles, buscarPorRol("VENTAS"), r.getSubtotal());

        if (r.getIva().compareTo(BigDecimal.ZERO) > 0) {
            agregarHaber(detalles, buscarPorRol("IVA_DEBITO"), r.getIva());
        }

        return detalles;
    }

    /**
     * Construye el asiento sugerido de una venta usando una distribución de
     * cobro explícita. Esto permite ventas en efectivo, banco, crédito o una
     * combinación de cobro inmediato + saldo a clientes, sin alterar Kardex.
     */
    public List<DetalleAsiento> sugerirVentaInventario(
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            DistribucionPago distribucion
    ) throws Exception {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero.");
        }

        if (distribucion == null) {
            throw new IllegalArgumentException("Debe indicar cómo se cobrará la venta.");
        }

        BigDecimal monto = cantidad
                .multiply(precioUnitario)
                .setScale(2, RoundingMode.HALF_UP);

        ResultadoIVA r = calculoIVAService.calcular(monto);

        if (distribucion.getTotal().compareTo(r.getTotal()) != 0) {
            throw new IllegalArgumentException(
                    "La distribución del cobro ($" + distribucion.getTotal()
                    + ") no coincide con el total de la venta ($"
                    + r.getTotal() + ")."
            );
        }

        List<DetalleAsiento> detalles = new ArrayList<>();

        if (distribucion.getEfectivo().compareTo(BigDecimal.ZERO) > 0) {
            agregarDebe(
                    detalles,
                    buscarPorCodigo("1.1.01.01"),
                    distribucion.getEfectivo()
            );
        }

        if (distribucion.getBanco().compareTo(BigDecimal.ZERO) > 0) {
            agregarDebe(
                    detalles,
                    buscarPorCodigo("1.1.01.02"),
                    distribucion.getBanco()
            );
        }

        if (distribucion.getCredito().compareTo(BigDecimal.ZERO) > 0) {
            agregarDebe(
                    detalles,
                    buscarPorCodigo("1.1.02.01"),
                    distribucion.getCredito()
            );
        }

        agregarHaber(detalles, buscarPorRol("VENTAS"), r.getSubtotal());

        if (r.getIva().compareTo(BigDecimal.ZERO) > 0) {
            agregarHaber(detalles, buscarPorRol("IVA_DEBITO"), r.getIva());
        }

        return detalles;
    }

    private List<DetalleAsiento> sugerirCompraActivo(
            String texto,
            BigDecimal monto
    ) throws Exception {

        ResultadoIVA r = calculoIVAService.calcular(monto);
        List<DetalleAsiento> detalles = new ArrayList<>();

        Cuenta activo;

        if (contiene(texto, "vehiculo", "automovil", "carro", "transporte")) {
            activo = buscarPorCodigo("1.2.01.03");
        } else if (contiene(texto,
                "computadora", "ordenador", "laptop", "impresora",
                "equipo de computo", "equipo informatico")) {
            activo = buscarPorCodigo("1.2.01.02");
        } else {
            activo = buscarPorCodigo("1.2.01.01");
        }

        agregarDebe(detalles, activo, r.getSubtotal());

        if (r.getIva().compareTo(BigDecimal.ZERO) > 0) {
            agregarDebe(detalles, buscarPorRol("IVA_CREDITO"), r.getIva());
        }

        String forma = detectarFormaPago(texto);

        if ("CREDITO".equals(forma)) {
            agregarHaber(detalles, buscarPorCodigo("2.1.01.02"), r.getTotal());
        } else if ("BANCO".equals(forma)) {
            agregarHaber(detalles, buscarPorCodigo("1.1.01.02"), r.getTotal());
        } else {
            agregarHaber(detalles, buscarPorCodigo("1.1.01.01"), r.getTotal());
        }

        return detalles;
    }

    private List<DetalleAsiento> sugerirGasto(String texto, BigDecimal monto) throws Exception {
        List<DetalleAsiento> detalles = new ArrayList<>();
        Cuenta gasto;

        if (contiene(texto, "publicidad", "promocion")) {
            gasto = buscarPorRol("GASTOS_VENTA");
        } else if (contiene(texto, "interes", "bancario", "comision")) {
            gasto = buscarPorRol("GASTOS_FINANCIEROS");
        } else {
            gasto = buscarPorRol("GASTOS_ADMIN");
        }

        agregarDebe(detalles, gasto, monto);
        agregarHaber(detalles, buscarCuentaEfectivo(texto), monto);
        return detalles;
    }

    private List<DetalleAsiento> sugerirCobro(String texto, BigDecimal monto) throws Exception {
        List<DetalleAsiento> detalles = new ArrayList<>();
        agregarDebe(detalles, buscarCuentaEfectivo(texto), monto);
        agregarHaber(detalles, buscarPorCodigo("1.1.02.01"), monto);
        return detalles;
    }

    private List<DetalleAsiento> sugerirPagoProveedor(String texto, BigDecimal monto) throws Exception {
        List<DetalleAsiento> detalles = new ArrayList<>();
        agregarDebe(detalles, buscarPorCodigo("2.1.01.01"), monto);
        agregarHaber(detalles, buscarCuentaEfectivo(texto), monto);
        return detalles;
    }

    private Cuenta buscarCuentaEfectivo(String texto) throws Exception {
        String forma = detectarFormaPago(texto);

        if ("BANCO".equals(forma)) {
            return buscarPorCodigo("1.1.01.02");
        }

        return buscarPorCodigo("1.1.01.01");
    }

    private Cuenta buscarPorRol(String rol) throws Exception {
        Cuenta cuenta = cuentaDAO.buscarPorRol(rol);
        if (cuenta == null) {
            throw new IllegalStateException("No existe una cuenta disponible con el rol: " + rol);
        }
        return cuenta;
    }

    private Cuenta buscarPorCodigo(String codigo) throws Exception {
        for (Cuenta cuenta : cuentaDAO.listarCuentasMovimiento()) {
            if (codigo.equals(cuenta.getCodigo())) {
                return cuenta;
            }
        }
        throw new IllegalStateException("No se encontró la cuenta " + codigo + " en el catálogo.");
    }

    private void agregarDebe(List<DetalleAsiento> detalles, Cuenta cuenta, BigDecimal monto) {
        DetalleAsiento d = new DetalleAsiento();
        d.setIdCuenta(cuenta.getIdCuenta());
        d.setDescripcion(cuenta.getNombre());
        d.setDebe(monto.setScale(2, RoundingMode.HALF_UP));
        d.setHaber(BigDecimal.ZERO.setScale(2));
        detalles.add(d);
    }

    private void agregarHaber(List<DetalleAsiento> detalles, Cuenta cuenta, BigDecimal monto) {
        DetalleAsiento d = new DetalleAsiento();
        d.setIdCuenta(cuenta.getIdCuenta());
        d.setDescripcion(cuenta.getNombre());
        d.setDebe(BigDecimal.ZERO.setScale(2));
        d.setHaber(monto.setScale(2, RoundingMode.HALF_UP));
        detalles.add(d);
    }

    private BigDecimal extraerMontoGeneral(String texto) {
        String limpio = normalizar(texto);

        Matcher conDolar = Pattern.compile(
                "\\$\\s*(-?\\d+(?:[\\.,]\\d{1,2})?)"
        ).matcher(limpio);

        if (conDolar.find()) {
            return parsearNumero(conDolar.group(1), 2);
        }

        Matcher porMonto = Pattern.compile(
                "(?:por|de|total)\\s+(-?\\d+(?:[\\.,]\\d{1,2})?)"
        ).matcher(limpio);

        if (porMonto.find()) {
            return parsearNumero(porMonto.group(1), 2);
        }

        return null;
    }

    private BigDecimal parsearNumero(String valor, int escala) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String limpio = valor.trim();

        /*
         * Para los conceptos del sistema:
         * 600,50 -> 600.50
         * 600.50 -> 600.50
         * No se aceptan separadores de miles dentro del concepto.
         */
        if (limpio.contains(",") && limpio.contains(".")) {
            throw new IllegalArgumentException(
                    "Usa solamente punto o coma como separador decimal, no ambos."
            );
        }

        limpio = limpio.replace(',', '.');

        try {
            return new BigDecimal(limpio)
                    .setScale(escala, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El monto o cantidad escrito no tiene un formato válido."
            );
        }
    }

    private boolean contiene(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(normalizar(palabra))) {
                return true;
            }
        }
        return false;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        String n = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").trim();
    }
}
