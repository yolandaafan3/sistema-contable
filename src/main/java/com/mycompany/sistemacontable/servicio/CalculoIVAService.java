package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.ConfiguracionContableDAO;
import com.mycompany.sistemacontable.modelo.ConfiguracionContable;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoIVAService {

    private final ConfiguracionContableDAO configuracionDAO;

    public CalculoIVAService() {
        configuracionDAO = new ConfiguracionContableDAO();
    }

    public ResultadoIVA calcular(BigDecimal monto) {

        if (monto == null) {
            throw new IllegalArgumentException(
                    "El monto no puede estar vacio."
            );
        }

        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El monto no puede ser negativo."
            );
        }

        ConfiguracionContable configuracion =
                configuracionDAO.obtenerConfiguracion();

        if (configuracion == null) {
            throw new IllegalStateException(
                    "No existe configuracion contable."
            );
        }

        BigDecimal porcentaje =
                configuracion.getPorcentajeIva()
                        .divide(
                                new BigDecimal("100"),
                                10,
                                RoundingMode.HALF_UP
                        );

        BigDecimal subtotal;
        BigDecimal iva;
        BigDecimal total;

        if ("INCLUIDO".equals(
                configuracion.getTipoIva()
        )) {

            BigDecimal divisor =
                    BigDecimal.ONE.add(porcentaje);

            subtotal =
                    monto.divide(
                            divisor,
                            2,
                            RoundingMode.HALF_UP
                    );

            iva =
                    monto.subtract(subtotal)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            total =
                    monto.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

        } else if ("MAS_IVA".equals(
                configuracion.getTipoIva()
        )) {

            subtotal =
                    monto.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            iva =
                    subtotal.multiply(porcentaje)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            total =
                    subtotal.add(iva)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

        } else {

            throw new IllegalStateException(
                    "Tipo de IVA no reconocido."
            );
        }

        return new ResultadoIVA(
                subtotal,
                iva,
                total
        );
    }
    
    public ResultadoIVA calcularSobreBase(
        BigDecimal subtotal
) {

    if (subtotal == null) {

        throw new IllegalArgumentException(
                "El subtotal no puede estar vacio."
        );
    }


    if (subtotal.compareTo(
            BigDecimal.ZERO
    ) < 0) {

        throw new IllegalArgumentException(
                "El subtotal no puede ser negativo."
        );
    }


    ConfiguracionContable configuracion =
            configuracionDAO.obtenerConfiguracion();


    if (configuracion == null) {

        throw new IllegalStateException(
                "No existe configuracion contable."
        );
    }


    BigDecimal porcentaje =
            configuracion
                    .getPorcentajeIva()
                    .divide(
                            new BigDecimal("100"),
                            10,
                            RoundingMode.HALF_UP
                    );


    BigDecimal base =
            subtotal.setScale(
                    2,
                    RoundingMode.HALF_UP
            );


    BigDecimal iva =
            base.multiply(
                    porcentaje
            )
            .setScale(
                    2,
                    RoundingMode.HALF_UP
            );


    BigDecimal total =
            base.add(
                    iva
            )
            .setScale(
                    2,
                    RoundingMode.HALF_UP
            );


    return new ResultadoIVA(
            base,
            iva,
            total
    );
}
    
}