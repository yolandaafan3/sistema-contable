package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializacionSistemaService {

    public void prepararCatalogoBase() {

        Connection conexion = null;

        try {

            conexion = Conexion.conectar();

            if (conexion == null) {

                throw new SQLException(
                        "No se pudo conectar con la base de datos."
                );
            }

            conexion.setAutoCommit(false);


            // =================================================
            // ACTIVO CORRIENTE
            // =================================================

            int efectivo = asegurarCuenta(
                    conexion,
                    "1.1.01",
                    "Efectivo y Equivalentes",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "EFECTIVO",
                    null,
                    false
            );


            asegurarCuenta(
                    conexion,
                    "1.1.01.01",
                    "Caja",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "NINGUNO",
                    efectivo,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "1.1.01.02",
                    "Banco",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "NINGUNO",
                    efectivo,
                    true
            );


            int cuentasCobrar = asegurarCuenta(
                    conexion,
                    "1.1.02",
                    "Cuentas por Cobrar",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "CUENTAS_COBRAR",
                    null,
                    false
            );


            asegurarCuenta(
                    conexion,
                    "1.1.02.01",
                    "Clientes",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "NINGUNO",
                    cuentasCobrar,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "1.1.03",
                    "Inventario",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "INVENTARIO",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "1.1.04",
                    "IVA Crédito Fiscal",
                    "ACTIVO",
                    "CORRIENTE",
                    "DEUDORA",
                    "IVA_CREDITO",
                    null,
                    true
            );


            // =================================================
            // ACTIVO NO CORRIENTE
            // =================================================

            int propiedadPlantaEquipo = asegurarCuenta(
                    conexion,
                    "1.2.01",
                    "Propiedad, Planta y Equipo",
                    "ACTIVO",
                    "NO_CORRIENTE",
                    "DEUDORA",
                    "PROPIEDAD_PLANTA_EQUIPO",
                    null,
                    false
            );


            asegurarCuenta(
                    conexion,
                    "1.2.01.01",
                    "Mobiliario de Oficina",
                    "ACTIVO",
                    "NO_CORRIENTE",
                    "DEUDORA",
                    "NINGUNO",
                    propiedadPlantaEquipo,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "1.2.01.02",
                    "Equipo de Cómputo",
                    "ACTIVO",
                    "NO_CORRIENTE",
                    "DEUDORA",
                    "NINGUNO",
                    propiedadPlantaEquipo,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "1.2.01.03",
                    "Equipo de Transporte",
                    "ACTIVO",
                    "NO_CORRIENTE",
                    "DEUDORA",
                    "NINGUNO",
                    propiedadPlantaEquipo,
                    true
            );


            // =================================================
            // PASIVO CORRIENTE
            // =================================================

            int cuentasPagar = asegurarCuenta(
                    conexion,
                    "2.1.01",
                    "Cuentas por Pagar",
                    "PASIVO",
                    "CORRIENTE",
                    "ACREEDORA",
                    "CUENTAS_PAGAR",
                    null,
                    false
            );


            asegurarCuenta(
                    conexion,
                    "2.1.01.01",
                    "Proveedores",
                    "PASIVO",
                    "CORRIENTE",
                    "ACREEDORA",
                    "NINGUNO",
                    cuentasPagar,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "2.1.01.02",
                    "Acreedores Varios",
                    "PASIVO",
                    "CORRIENTE",
                    "ACREEDORA",
                    "NINGUNO",
                    cuentasPagar,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "2.1.02",
                    "IVA Débito Fiscal",
                    "PASIVO",
                    "CORRIENTE",
                    "ACREEDORA",
                    "IVA_DEBITO",
                    null,
                    true
            );


            // =================================================
            // PASIVO NO CORRIENTE
            // =================================================

            asegurarCuenta(
                    conexion,
                    "2.2.01",
                    "Préstamo Bancario",
                    "PASIVO",
                    "NO_CORRIENTE",
                    "ACREEDORA",
                    "PRESTAMO_BANCARIO",
                    null,
                    true
            );


            // =================================================
            // PATRIMONIO
            // =================================================

            asegurarCuenta(
                    conexion,
                    "3.1.01",
                    "Capital Social",
                    "PATRIMONIO",
                    "PATRIMONIO",
                    "ACREEDORA",
                    "CAPITAL_SOCIAL",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "3.2.01",
                    "Utilidad del Ejercicio",
                    "PATRIMONIO",
                    "PATRIMONIO",
                    "ACREEDORA",
                    "UTILIDAD_EJERCICIO",
                    null,
                    false
            );


            // =================================================
            // INGRESOS
            // =================================================

            asegurarCuenta(
                    conexion,
                    "4.1.01",
                    "Ventas",
                    "INGRESO",
                    "INGRESO",
                    "ACREEDORA",
                    "VENTAS",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "4.1.02",
                    "Devolución sobre Ventas",
                    "INGRESO",
                    "INGRESO",
                    "DEUDORA",
                    "DEVOLUCION_VENTAS",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "4.1.03",
                    "Descuentos sobre Ventas",
                    "INGRESO",
                    "INGRESO",
                    "DEUDORA",
                    "NINGUNO",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "4.2.01",
                    "Otros Productos / Productos Financieros",
                    "INGRESO",
                    "INGRESO",
                    "ACREEDORA",
                    "NINGUNO",
                    null,
                    true
            );


            // =================================================
            // COMPRAS / COSTOS
            // =================================================

            asegurarCuenta(
                    conexion,
                    "5.1.01",
                    "Compras",
                    "COSTO",
                    "COSTO",
                    "DEUDORA",
                    "COMPRAS",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "5.1.02",
                    "Devolución sobre Compras",
                    "COSTO",
                    "COSTO",
                    "ACREEDORA",
                    "DEVOLUCION_COMPRAS",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "5.1.03",
                    "Gastos sobre Compras",
                    "COSTO",
                    "COSTO",
                    "DEUDORA",
                    "GASTOS_COMPRA",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "5.1.04",
                    "Descuentos sobre Compras",
                    "COSTO",
                    "COSTO",
                    "ACREEDORA",
                    "NINGUNO",
                    null,
                    true
            );


            // =================================================
            // GASTOS
            // =================================================

            asegurarCuenta(
                    conexion,
                    "6.1.01",
                    "Gastos Administrativos",
                    "GASTO",
                    "ADMINISTRATIVO",
                    "DEUDORA",
                    "GASTOS_ADMIN",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "6.2.01",
                    "Gastos de Venta",
                    "GASTO",
                    "VENTA",
                    "DEUDORA",
                    "GASTOS_VENTA",
                    null,
                    true
            );


            asegurarCuenta(
                    conexion,
                    "6.3.01",
                    "Gastos Financieros",
                    "GASTO",
                    "FINANCIERO",
                    "DEUDORA",
                    "GASTOS_FINANCIEROS",
                    null,
                    true
            );


            conexion.commit();


            System.out.println(
                    "✅ Catálogo contable base verificado correctamente."
            );


        } catch (Exception e) {

            if (conexion != null) {

                try {

                    conexion.rollback();

                } catch (SQLException ex) {

                    System.out.println(
                            "Error en rollback: "
                            + ex.getMessage()
                    );
                }
            }


            throw new RuntimeException(
                    "No se pudo preparar el catálogo contable: "
                    + e.getMessage(),
                    e
            );


        } finally {

            if (conexion != null) {

                try {

                    conexion.setAutoCommit(true);

                    conexion.close();

                } catch (SQLException e) {

                    System.out.println(
                            "Error al cerrar conexión: "
                            + e.getMessage()
                    );
                }
            }
        }
    }


    private int asegurarCuenta(
            Connection conexion,
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre,
            boolean permiteMovimiento
    ) throws SQLException {

        Integer idCuenta =
                buscarIdPorCodigo(
                        conexion,
                        codigo
                );


        if (idCuenta == null) {

            return insertarCuenta(
                    conexion,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rolReporte,
                    idCuentaPadre,
                    permiteMovimiento
            );
        }


        actualizarCuentaBase(
                conexion,
                idCuenta,
                codigo,
                nombre,
                tipo,
                clasificacion,
                naturaleza,
                rolReporte,
                idCuentaPadre,
                permiteMovimiento
        );


        return idCuenta;
    }


    private Integer buscarIdPorCodigo(
            Connection conexion,
            String codigo
    ) throws SQLException {

        String sql = """
                SELECT id_cuenta
                FROM catalogo_cuentas
                WHERE codigo = ?
                LIMIT 1
                """;


        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    codigo
            );


            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "id_cuenta"
                    );
                }
            }
        }


        return null;
    }


    private int insertarCuenta(
            Connection conexion,
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre,
            boolean permiteMovimiento
    ) throws SQLException {

        String sql = """
                INSERT INTO catalogo_cuentas (
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rol_reporte,
                    id_cuenta_padre,
                    permite_movimiento,
                    activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)
                """;


        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            cargarParametros(
                    ps,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rolReporte,
                    idCuentaPadre,
                    permiteMovimiento
            );


            ps.executeUpdate();


            try (
                    ResultSet rs =
                            ps.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }
        }


        throw new SQLException(
                "No se pudo obtener el ID de la cuenta "
                + codigo
                + "."
        );
    }


    private void actualizarCuentaBase(
            Connection conexion,
            int idCuenta,
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre,
            boolean permiteMovimiento
    ) throws SQLException {

        String sql = """
                UPDATE catalogo_cuentas
                SET
                    codigo = ?,
                    nombre = ?,
                    tipo = ?,
                    clasificacion = ?,
                    naturaleza = ?,
                    rol_reporte = ?,
                    id_cuenta_padre = ?,
                    permite_movimiento = ?,
                    activo = TRUE
                WHERE id_cuenta = ?
                """;


        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            cargarParametros(
                    ps,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rolReporte,
                    idCuentaPadre,
                    permiteMovimiento
            );


            ps.setInt(
                    9,
                    idCuenta
            );


            ps.executeUpdate();
        }
    }


    private void cargarParametros(
            PreparedStatement ps,
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre,
            boolean permiteMovimiento
    ) throws SQLException {

        ps.setString(
                1,
                codigo
        );

        ps.setString(
                2,
                nombre
        );

        ps.setString(
                3,
                tipo
        );

        ps.setString(
                4,
                clasificacion
        );

        ps.setString(
                5,
                naturaleza
        );

        ps.setString(
                6,
                rolReporte
        );


        if (idCuentaPadre == null) {

            ps.setNull(
                    7,
                    java.sql.Types.INTEGER
            );

        } else {

            ps.setInt(
                    7,
                    idCuentaPadre
            );
        }


        ps.setBoolean(
                8,
                permiteMovimiento
        );
    }
}