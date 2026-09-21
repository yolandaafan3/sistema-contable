package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.EmpresaDAO;
import com.mycompany.sistemacontable.modelo.Empresa;

public class EmpresaService {

    private final EmpresaDAO empresaDAO;

    public EmpresaService() {
        empresaDAO = new EmpresaDAO();
    }

    public Empresa obtener(int idEmpresa) {

        Empresa empresa = empresaDAO.buscarPorId(idEmpresa);

        if (empresa == null) {
            throw new IllegalStateException(
                    "No se encontró la empresa configurada."
            );
        }

        return empresa;
    }

    public void actualizar(Empresa empresa) {

        if (empresa == null) {
            throw new IllegalArgumentException(
                    "La empresa no puede ser nula."
            );
        }

        if (empresa.getNombre() == null
                || empresa.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la empresa es obligatorio."
            );
        }

        empresa.setNombre(empresa.getNombre().trim());
        empresa.setNit(limpiar(empresa.getNit()));
        empresa.setNrc(limpiar(empresa.getNrc()));
        empresa.setGiroComercial(limpiar(empresa.getGiroComercial()));
        empresa.setDireccion(limpiar(empresa.getDireccion()));
        empresa.setTelefono(limpiar(empresa.getTelefono()));
        empresa.setCorreo(limpiar(empresa.getCorreo()));

        empresaDAO.actualizar(empresa);
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }

        String limpio = valor.trim();

        return limpio.isEmpty() ? null : limpio;
    }
}