package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.Cuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class CuentaDAO {

    public List<Cuenta> listarCuentasMovimiento() {

        List<Cuenta> cuentas = new ArrayList<>();

        String sql = """
                SELECT
                    id_cuenta,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rol_reporte,
                    id_cuenta_padre,
                    permite_movimiento,
                    activo
                FROM catalogo_cuentas
                WHERE activo = TRUE
                  AND permite_movimiento = TRUE
                ORDER BY codigo
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                cuentas.add(
                        construirCuenta(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar cuentas: "
                    + e.getMessage(),
                    e
            );
        }

        return cuentas;
    }


    public List<Cuenta> listarTodas(
            boolean incluirInactivas
    ) {

        List<Cuenta> cuentas =
                new ArrayList<>();

        String sql;

        if (incluirInactivas) {

            sql = """
                    SELECT
                        id_cuenta,
                        codigo,
                        nombre,
                        tipo,
                        clasificacion,
                        naturaleza,
                        rol_reporte,
                        id_cuenta_padre,
                        permite_movimiento,
                        activo
                    FROM catalogo_cuentas
                    ORDER BY codigo
                    """;

        } else {

            sql = """
                    SELECT
                        id_cuenta,
                        codigo,
                        nombre,
                        tipo,
                        clasificacion,
                        naturaleza,
                        rol_reporte,
                        id_cuenta_padre,
                        permite_movimiento,
                        activo
                    FROM catalogo_cuentas
                    WHERE activo = TRUE
                    ORDER BY codigo
                    """;
        }

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                cuentas.add(
                        construirCuenta(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener catalogo de cuentas: "
                    + e.getMessage(),
                    e
            );
        }

        return cuentas;
    }


    public Cuenta buscarPorRol(
            String rolReporte
    ) {

        String sql = """
                SELECT
                    id_cuenta,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rol_reporte,
                    id_cuenta_padre,
                    permite_movimiento,
                    activo
                FROM catalogo_cuentas
                WHERE rol_reporte = ?
                  AND activo = TRUE
                  AND permite_movimiento = TRUE
                ORDER BY codigo
                LIMIT 1
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    rolReporte
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return construirCuenta(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar cuenta por rol: "
                    + e.getMessage(),
                    e
            );
        }

        return null;
    }


    public Cuenta buscarPorCodigo(
            String codigo
    ) {

        String sql = """
                SELECT
                    id_cuenta,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rol_reporte,
                    id_cuenta_padre,
                    permite_movimiento,
                    activo
                FROM catalogo_cuentas
                WHERE codigo = ?
                  AND activo = TRUE
                LIMIT 1
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

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

                    return construirCuenta(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar cuenta: "
                    + e.getMessage(),
                    e
            );
        }

        return null;
    }


    public Cuenta buscarPorId(
            int idCuenta
    ) {

        String sql = """
                SELECT
                    id_cuenta,
                    codigo,
                    nombre,
                    tipo,
                    clasificacion,
                    naturaleza,
                    rol_reporte,
                    id_cuenta_padre,
                    permite_movimiento,
                    activo
                FROM catalogo_cuentas
                WHERE id_cuenta = ?
                LIMIT 1
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idCuenta
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return construirCuenta(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar cuenta por ID: "
                    + e.getMessage(),
                    e
            );
        }

        return null;
    }


    public boolean existeCodigo(
            String codigo,
            Integer ignorarId
    ) {

        String sql;

        if (ignorarId == null) {

            sql = """
                    SELECT COUNT(*) AS cantidad
                    FROM catalogo_cuentas
                    WHERE codigo = ?
                    """;

        } else {

            sql = """
                    SELECT COUNT(*) AS cantidad
                    FROM catalogo_cuentas
                    WHERE codigo = ?
                      AND id_cuenta <> ?
                    """;
        }

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    codigo
            );

            if (ignorarId != null) {

                ps.setInt(
                        2,
                        ignorarId
                );
            }

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "cantidad"
                    ) > 0;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al comprobar codigo de cuenta: "
                    + e.getMessage(),
                    e
            );
        }

        return false;
    }


    public int insertar(
            Cuenta cuenta
    ) {

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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            cargarParametros(
                    ps,
                    cuenta
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

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear cuenta: "
                    + e.getMessage(),
                    e
            );
        }

        throw new RuntimeException(
                "No se pudo obtener el ID de la cuenta."
        );
    }


    public void actualizar(
            Cuenta cuenta
    ) {

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
                    activo = ?
                WHERE id_cuenta = ?
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            cargarParametros(
                    ps,
                    cuenta
            );

            ps.setInt(
                    10,
                    cuenta.getIdCuenta()
            );

            int filas =
                    ps.executeUpdate();

            if (filas != 1) {

                throw new SQLException(
                        "La cuenta no pudo ser actualizada."
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al actualizar cuenta: "
                    + e.getMessage(),
                    e
            );
        }
    }


    public void actualizarEstado(
            int idCuenta,
            boolean activo
    ) {

        String sql = """
                UPDATE catalogo_cuentas
                SET activo = ?
                WHERE id_cuenta = ?
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setBoolean(
                    1,
                    activo
            );

            ps.setInt(
                    2,
                    idCuenta
            );

            int filas =
                    ps.executeUpdate();

            if (filas != 1) {

                throw new SQLException(
                        "No se pudo cambiar el estado de la cuenta."
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al cambiar estado de cuenta: "
                    + e.getMessage(),
                    e
            );
        }
    }


    public boolean tieneMovimientos(
            int idCuenta
    ) {

        String sql = """
                SELECT COUNT(*) AS cantidad
                FROM detalle_asientos
                WHERE id_cuenta = ?
                """;

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idCuenta
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "cantidad"
                    ) > 0;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al comprobar movimientos de cuenta: "
                    + e.getMessage(),
                    e
            );
        }

        return false;
    }


    private void cargarParametros(
            PreparedStatement ps,
            Cuenta cuenta
    ) throws SQLException {

        ps.setString(
                1,
                cuenta.getCodigo()
        );

        ps.setString(
                2,
                cuenta.getNombre()
        );

        ps.setString(
                3,
                cuenta.getTipo()
        );

        ps.setString(
                4,
                cuenta.getClasificacion()
        );

        ps.setString(
                5,
                cuenta.getNaturaleza()
        );

        ps.setString(
                6,
                cuenta.getRolReporte()
        );


        if (cuenta.getIdCuentaPadre() != null) {

            ps.setInt(
                    7,
                    cuenta.getIdCuentaPadre()
            );

        } else {

            ps.setNull(
                    7,
                    java.sql.Types.INTEGER
            );
        }


        ps.setBoolean(
                8,
                cuenta.isPermiteMovimiento()
        );

        ps.setBoolean(
                9,
                cuenta.isActivo()
        );
    }


    private Cuenta construirCuenta(
            ResultSet rs
    ) throws SQLException {

        Cuenta cuenta =
                new Cuenta();

        cuenta.setIdCuenta(
                rs.getInt(
                        "id_cuenta"
                )
        );

        cuenta.setCodigo(
                rs.getString(
                        "codigo"
                )
        );

        cuenta.setNombre(
                rs.getString(
                        "nombre"
                )
        );

        cuenta.setTipo(
                rs.getString(
                        "tipo"
                )
        );

        cuenta.setClasificacion(
                rs.getString(
                        "clasificacion"
                )
        );

        cuenta.setNaturaleza(
                rs.getString(
                        "naturaleza"
                )
        );

        cuenta.setRolReporte(
                rs.getString(
                        "rol_reporte"
                )
        );


        Object idPadre =
                rs.getObject(
                        "id_cuenta_padre"
                );

        if (idPadre != null) {

            cuenta.setIdCuentaPadre(
                    ((Number) idPadre)
                            .intValue()
            );

        } else {

            cuenta.setIdCuentaPadre(
                    null
            );
        }


        cuenta.setPermiteMovimiento(
                rs.getBoolean(
                        "permite_movimiento"
                )
        );

        cuenta.setActivo(
                rs.getBoolean(
                        "activo"
                )
        );

        return cuenta;
    }
}