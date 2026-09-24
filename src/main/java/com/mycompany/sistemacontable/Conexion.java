package com.mycompany.sistemacontable;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://contapromax-mysql-conta-pro-max.g.aivencloud.com:15485/sistema_contable"
            + "?sslMode=REQUIRED"
            + "&serverTimezone=UTC"
            + "&connectTimeout=10000"
            + "&socketTimeout=15000";

    private static final String USUARIO = "avnadmin";

    private static final String CONTRASENA =
            "AVNS_VIBXOYsPVjo03C0nvZO";

    private static final HikariDataSource dataSource;

    static {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(URL);
        config.setUsername(USUARIO);
        config.setPassword(CONTRASENA);

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);

        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        config.setPoolName("ContaProMaxPool");

        dataSource = new HikariDataSource(config);

        System.out.println(
                "Pool de conexiones ContaProMax iniciado correctamente."
        );
    }

    public static Connection conectar() {

        try {

            return dataSource.getConnection();

        } catch (SQLException e) {

            System.err.println(
                    "Error obteniendo conexion: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return null;
        }
    }
}