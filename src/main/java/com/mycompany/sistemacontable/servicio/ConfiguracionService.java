package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.ConfiguracionContableDAO;
import com.mycompany.sistemacontable.modelo.ConfiguracionContable;

import java.math.BigDecimal;

public class ConfiguracionService {

    private final ConfiguracionContableDAO configuracionDAO;

    public ConfiguracionService() {
        configuracionDAO = new ConfiguracionContableDAO();
    }

    public ConfiguracionContable obtener() {
        ConfiguracionContable configuracion = configuracionDAO.obtenerConfiguracion();
        if (configuracion == null) {
            throw new IllegalStateException("No existe configuración contable.");
        }
        return configuracion;
    }

    public void actualizarIva(BigDecimal porcentaje, String tipoIva) {
        if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(new BigDecimal("100")) >= 0) {
            throw new IllegalArgumentException("El porcentaje de IVA debe estar entre 0 y 100.");
        }

        if (!"INCLUIDO".equals(tipoIva) && !"MAS_IVA".equals(tipoIva)) {
            throw new IllegalArgumentException("El tipo de IVA debe ser INCLUIDO o MAS_IVA.");
        }

        ConfiguracionContable actual = obtener();
        configuracionDAO.actualizarIva(
                actual.getIdConfiguracion(),
                porcentaje,
                tipoIva
        );
    }
}
