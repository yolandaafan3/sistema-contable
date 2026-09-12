package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReinicioSistemaService {

    public String reiniciarDatosPrueba() {

        Connection conexion = null;

        try {

            conexion =
                    Conexion.conectar();


            if (conexion == null) {

                throw new SQLException(
                        "No se pudo conectar con la base de datos."
                );
            }


            conexion.setAutoCommit(
                    false
            );


            // =================================================
            // 1. ELIMINAR CAPAS PEPS
            // =================================================

            int capasPeps =
                    ejecutar(
                            conexion,
                            "DELETE FROM capas_peps"
                    );


            // =================================================
            // 2. ELIMINAR KARDEX
            // =================================================

            int movimientosKardex =
                    ejecutar(
                            conexion,
                            "DELETE FROM kardex"
                    );


            // =================================================
            // 3. ELIMINAR DETALLES DE ASIENTOS
            // =================================================

            int detallesAsientos =
                    ejecutar(
                            conexion,
                            "DELETE FROM detalle_asientos"
                    );


            // =================================================
            // 4. ELIMINAR ASIENTOS
            // =================================================

            int asientos =
                    ejecutar(
                            conexion,
                            "DELETE FROM asientos_contables"
                    );


            // =================================================
            // 5. ELIMINAR OPERACIONES
            // =================================================

            int operaciones =
                    ejecutar(
                            conexion,
                            "DELETE FROM operaciones"
                    );


            // =================================================
            // 6. REINICIAR EXISTENCIAS DE PRODUCTOS
            // =================================================

            ejecutar(
                    conexion,
                    """
                    UPDATE productos
                    SET
                        existencia_inicial = 0,
                        existencia_actual = 0
                    """
            );


            // =================================================
            // COMMIT
            // =================================================

            conexion.commit();


            return """
                   Sistema limpio correctamente.

                   Operaciones eliminadas: %d
                   Asientos eliminados: %d
                   Detalles eliminados: %d
                   Movimientos Kardex eliminados: %d
                   Capas PEPS eliminadas: %d

                   Las existencias de productos quedaron en 0.

                   El catálogo de cuentas, empresa,
                   configuración, período y productos
                   NO fueron eliminados.
                   """
                    .formatted(
                            operaciones,
                            asientos,
                            detallesAsientos,
                            movimientosKardex,
                            capasPeps
                    );


        } catch (Exception e) {

            if (conexion != null) {

                try {

                    conexion.rollback();

                } catch (SQLException ex) {

                    System.out.println(
                            "Error al realizar rollback: "
                            + ex.getMessage()
                    );
                }
            }


            throw new RuntimeException(
                    "No se pudo limpiar el sistema: "
                    + e.getMessage(),
                    e
            );


        } finally {

            if (conexion != null) {

                try {

                    conexion.setAutoCommit(
                            true
                    );


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


    private int ejecutar(
            Connection conexion,
            String sql
    ) throws SQLException {

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        )
        ) {

            return ps.executeUpdate();
        }
    }
}