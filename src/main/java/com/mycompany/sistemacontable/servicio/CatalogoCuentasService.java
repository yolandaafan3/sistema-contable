package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.Cuenta;

import java.util.List;
import java.util.Set;

public class CatalogoCuentasService {

    private final CuentaDAO cuentaDAO;

    private static final Set<String> TIPOS =
            Set.of(
                    "ACTIVO",
                    "PASIVO",
                    "PATRIMONIO",
                    "INGRESO",
                    "COSTO",
                    "GASTO"
            );

    private static final Set<String> NATURALEZAS =
            Set.of(
                    "DEUDORA",
                    "ACREEDORA"
            );

    private static final Set<String> ROLES =
            Set.of(
                    "NINGUNO",

                    "EFECTIVO",
                    "CUENTAS_COBRAR",
                    "INVENTARIO",
                    "IVA_CREDITO",

                    "PROPIEDAD_PLANTA_EQUIPO",

                    "CUENTAS_PAGAR",
                    "IVA_DEBITO",
                    "PRESTAMO_BANCARIO",

                    "CAPITAL_SOCIAL",

                    "VENTAS",
                    "DEVOLUCION_VENTAS",

                    "COMPRAS",
                    "DEVOLUCION_COMPRAS",
                    "GASTOS_COMPRA",

                    "GASTOS_ADMIN",
                    "GASTOS_VENTA",
                    "GASTOS_FINANCIEROS",

                    "UTILIDAD_EJERCICIO"
            );


    public CatalogoCuentasService() {

        cuentaDAO =
                new CuentaDAO();
    }


    public List<Cuenta> listarCatalogo() {

        return cuentaDAO.listarTodas(
                true
        );
    }


    public List<Cuenta> listarActivas() {

        return cuentaDAO.listarTodas(
                false
        );
    }


    public Cuenta buscarPorId(
            int idCuenta
    ) {

        return cuentaDAO.buscarPorId(
                idCuenta
        );
    }


    public int crearCuenta(
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre,
            boolean permiteMovimiento
    ) {

        validarDatos(
                null,
                codigo,
                nombre,
                tipo,
                clasificacion,
                naturaleza,
                rolReporte,
                idCuentaPadre
        );


        Cuenta cuenta =
                new Cuenta();

        cuenta.setCodigo(
                codigo.trim()
        );

        cuenta.setNombre(
                nombre.trim()
        );

        cuenta.setTipo(
                tipo
        );

        cuenta.setClasificacion(
                clasificacion
        );

        cuenta.setNaturaleza(
                naturaleza
        );

        cuenta.setRolReporte(
                rolReporte
        );

        cuenta.setIdCuentaPadre(
                idCuentaPadre
        );

        cuenta.setPermiteMovimiento(
                permiteMovimiento
        );

        cuenta.setActivo(
                true
        );


        return cuentaDAO.insertar(
                cuenta
        );
    }


    public void actualizarCuenta(
            int idCuenta,
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre,
            boolean permiteMovimiento
    ) {

        Cuenta existente =
                cuentaDAO.buscarPorId(
                        idCuenta
                );

        if (existente == null) {

            throw new IllegalArgumentException(
                    "La cuenta no existe."
            );
        }


        validarDatos(
                idCuenta,
                codigo,
                nombre,
                tipo,
                clasificacion,
                naturaleza,
                rolReporte,
                idCuentaPadre
        );


        if (cuentaDAO.tieneMovimientos(
                idCuenta
        )) {

            if (!existente.getCodigo()
                    .equals(codigo.trim())
                    ||
                !existente.getTipo()
                    .equals(tipo)
                    ||
                !existente.getClasificacion()
                    .equals(clasificacion)
                    ||
                !existente.getNaturaleza()
                    .equals(naturaleza)
                    ||
                !existente.getRolReporte()
                    .equals(rolReporte)) {

                throw new IllegalStateException(
                        "La cuenta ya tiene movimientos. "
                        + "No se puede cambiar su codigo, tipo, "
                        + "clasificacion, naturaleza ni rol contable."
                );
            }
        }


        existente.setCodigo(
                codigo.trim()
        );

        existente.setNombre(
                nombre.trim()
        );

        existente.setTipo(
                tipo
        );

        existente.setClasificacion(
                clasificacion
        );

        existente.setNaturaleza(
                naturaleza
        );

        existente.setRolReporte(
                rolReporte
        );

        existente.setIdCuentaPadre(
                idCuentaPadre
        );

        existente.setPermiteMovimiento(
                permiteMovimiento
        );


        cuentaDAO.actualizar(
                existente
        );
    }


    public void desactivarCuenta(
            int idCuenta
    ) {

        Cuenta cuenta =
                cuentaDAO.buscarPorId(
                        idCuenta
                );

        if (cuenta == null) {

            throw new IllegalArgumentException(
                    "La cuenta no existe."
            );
        }


        cuentaDAO.actualizarEstado(
                idCuenta,
                false
        );
    }


    public void reactivarCuenta(
            int idCuenta
    ) {

        Cuenta cuenta =
                cuentaDAO.buscarPorId(
                        idCuenta
                );

        if (cuenta == null) {

            throw new IllegalArgumentException(
                    "La cuenta no existe."
            );
        }


        cuentaDAO.actualizarEstado(
                idCuenta,
                true
        );
    }


    private void validarDatos(
            Integer idCuenta,
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            String naturaleza,
            String rolReporte,
            Integer idCuentaPadre
    ) {

        if (codigo == null
                || codigo.isBlank()) {

            throw new IllegalArgumentException(
                    "El codigo es obligatorio."
            );
        }


        if (nombre == null
                || nombre.isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la cuenta es obligatorio."
            );
        }


        if (!TIPOS.contains(
                tipo
        )) {

            throw new IllegalArgumentException(
                    "Tipo de cuenta no valido."
            );
        }


        if (!NATURALEZAS.contains(
                naturaleza
        )) {

            throw new IllegalArgumentException(
                    "Naturaleza contable no valida."
            );
        }


        if (!ROLES.contains(
                rolReporte
        )) {

            throw new IllegalArgumentException(
                    "Rol contable no valido."
            );
        }


        validarClasificacion(
                tipo,
                clasificacion
        );


        if (cuentaDAO.existeCodigo(
                codigo.trim(),
                idCuenta
        )) {

            throw new IllegalArgumentException(
                    "Ya existe una cuenta con el codigo "
                    + codigo.trim()
                    + "."
            );
        }


        if (idCuentaPadre != null) {

            if (idCuenta != null
                    && idCuentaPadre.equals(
                            idCuenta
                    )) {

                throw new IllegalArgumentException(
                        "Una cuenta no puede ser su propia cuenta padre."
                );
            }


            Cuenta padre =
                    cuentaDAO.buscarPorId(
                            idCuentaPadre
                    );

            if (padre == null) {

                throw new IllegalArgumentException(
                        "La cuenta padre no existe."
                );
            }
        }
    }


    private void validarClasificacion(
            String tipo,
            String clasificacion
    ) {

        boolean valida =
                switch (tipo) {

                    case "ACTIVO" ->
                        "CORRIENTE".equals(
                                clasificacion
                        )
                        ||
                        "NO_CORRIENTE".equals(
                                clasificacion
                        );

                    case "PASIVO" ->
                        "CORRIENTE".equals(
                                clasificacion
                        )
                        ||
                        "NO_CORRIENTE".equals(
                                clasificacion
                        );

                    case "PATRIMONIO" ->
                        "PATRIMONIO".equals(
                                clasificacion
                        );

                    case "INGRESO" ->
                        "INGRESO".equals(
                                clasificacion
                        );

                    case "COSTO" ->
                        "COSTO".equals(
                                clasificacion
                        );

                    case "GASTO" ->
                        "ADMINISTRATIVO".equals(
                                clasificacion
                        )
                        ||
                        "VENTA".equals(
                                clasificacion
                        )
                        ||
                        "FINANCIERO".equals(
                                clasificacion
                        )
                        ||
                        "OTRO".equals(
                                clasificacion
                        );

                    default ->
                        false;
                };


        if (!valida) {

            throw new IllegalArgumentException(
                    "La clasificacion "
                    + clasificacion
                    + " no corresponde al tipo "
                    + tipo
                    + "."
            );
        }
    }
}