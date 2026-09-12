package com.mycompany.sistemacontable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sistema_contable"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    private static final String USUARIO = "root";

    private static final String CONTRASENA = "";

    public static Connection conectar() {

        try {

            Connection conexion = DriverManager.getConnection(
                    URL,
                    USUARIO,
                    CONTRASENA
            );

            return conexion;

        } catch (SQLException e) {

            System.out.println(
                    "Error de conexion: " + e.getMessage()
            );

            return null;
        }
    }
}