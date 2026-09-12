package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.MayorizacionDAO;

import com.mycompany.sistemacontable.modelo.CuentaMayor;
import com.mycompany.sistemacontable.modelo.MovimientoMayor;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MayorizacionService {

    private final MayorizacionDAO mayorizacionDAO;
    private final PeriodoService periodoService;

    public MayorizacionService() {

        mayorizacionDAO =
                new MayorizacionDAO();

        periodoService =
                new PeriodoService();
    }


    /**
     * Devuelve las cuentas T agrupadas según la estructura utilizada
     * en la guía contable del proyecto.
     *
     * Ejemplos:
     * - Caja + Banco => Efectivo y Equivalentes
     * - Proveedores + Acreedores Varios => Cuentas por Pagar
     * - Mobiliario + Equipo de Cómputo + Equipo de Transporte
     *   => Propiedad, Planta y Equipo
     */
    public List<CuentaMayor> obtenerMayorizacion() {

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        List<CuentaMayor> cuentasDetalle =
                mayorizacionDAO.obtenerMayorizacion(
                        periodo.getIdPeriodo()
                );

        Map<String, CuentaMayor> agrupadas =
                new LinkedHashMap<>();


        for (CuentaMayor cuenta : cuentasDetalle) {

            GrupoMayor grupo =
                    obtenerGrupo(
                            cuenta
                    );

            CuentaMayor destino =
                    agrupadas.get(
                            grupo.codigo
                    );


            if (destino == null) {

                destino =
                        new CuentaMayor();

                destino.setCodigo(
                        grupo.codigo
                );

                destino.setNombre(
                        grupo.nombre
                );

                destino.setNaturaleza(
                        grupo.naturaleza != null
                                ? grupo.naturaleza
                                : cuenta.getNaturaleza()
                );

                destino.setTotalDebe(
                        BigDecimal.ZERO
                );

                destino.setTotalHaber(
                        BigDecimal.ZERO
                );

                agrupadas.put(
                        grupo.codigo,
                        destino
                );
            }


            destino.setTotalDebe(
                    destino.getTotalDebe()
                            .add(
                                    nuloACero(
                                            cuenta.getTotalDebe()
                                    )
                            )
            );


            destino.setTotalHaber(
                    destino.getTotalHaber()
                            .add(
                                    nuloACero(
                                            cuenta.getTotalHaber()
                                    )
                            )
            );


            for (MovimientoMayor movimiento
                    : cuenta.getMovimientos()) {

                destino.agregarMovimiento(
                        movimiento
                );
            }
        }


        List<CuentaMayor> resultado =
                new ArrayList<>(
                        agrupadas.values()
                );


        for (CuentaMayor cuenta : resultado) {

            cuenta.getMovimientos()
                    .sort(
                            Comparator
                                    .comparing(
                                            MovimientoMayor::getFecha,
                                            Comparator.nullsLast(
                                                    Comparator.naturalOrder()
                                            )
                                    )
                                    .thenComparingInt(
                                            MovimientoMayor::getNumeroAsiento
                                    )
                    );

            calcularSaldo(
                    cuenta
            );
        }


        resultado.sort(
                Comparator.comparingInt(
                        cuenta -> ordenGrupo(
                                cuenta.getCodigo()
                        )
                )
        );


        return resultado;
    }


    private GrupoMayor obtenerGrupo(
            CuentaMayor cuenta
    ) {

        String codigo =
                cuenta.getCodigo();


        if (codigo == null) {

            return new GrupoMayor(
                    "SIN_CODIGO",
                    cuenta.getNombre(),
                    cuenta.getNaturaleza()
            );
        }


        if (codigo.startsWith("1.1.01.")) {

            return new GrupoMayor(
                    "1.1.01",
                    "Efectivo y Equivalentes",
                    "DEUDORA"
            );
        }


        if (codigo.equals("1.1.03")) {

            return new GrupoMayor(
                    "1.1.03",
                    "Inventario",
                    "DEUDORA"
            );
        }


        if (codigo.equals("3.1.01")) {

            return new GrupoMayor(
                    "3.1.01",
                    "Capital Social",
                    "ACREEDORA"
            );
        }


        if (codigo.equals("5.1.01")) {

            return new GrupoMayor(
                    "5.1.01",
                    "Compras",
                    "DEUDORA"
            );
        }


        if (codigo.equals("1.1.04")) {

            return new GrupoMayor(
                    "1.1.04",
                    "IVA Crédito Fiscal",
                    "DEUDORA"
            );
        }


        if (codigo.equals("5.1.02")) {

            return new GrupoMayor(
                    "5.1.02",
                    "Devolución sobre Compras",
                    "ACREEDORA"
            );
        }


        if (codigo.startsWith("2.1.01.")) {

            return new GrupoMayor(
                    "2.1.01",
                    "Cuentas por Pagar",
                    "ACREEDORA"
            );
        }


        if (codigo.equals("4.1.01")) {

            return new GrupoMayor(
                    "4.1.01",
                    "Ventas",
                    "ACREEDORA"
            );
        }


        if (codigo.equals("4.1.02")) {

            return new GrupoMayor(
                    "4.1.02",
                    "Devolución sobre Ventas",
                    "DEUDORA"
            );
        }


        if (codigo.equals("2.1.02")) {

            return new GrupoMayor(
                    "2.1.02",
                    "IVA Débito Fiscal",
                    "ACREEDORA"
            );
        }


        if (codigo.startsWith("1.1.02.")) {

            return new GrupoMayor(
                    "1.1.02",
                    "Cuentas por Cobrar",
                    "DEUDORA"
            );
        }


        if (codigo.startsWith("1.2.01.")) {

            return new GrupoMayor(
                    "1.2.01",
                    "Propiedad, Planta y Equipo",
                    "DEUDORA"
            );
        }


        if (codigo.equals("6.3.01")) {

            return new GrupoMayor(
                    "6.3.01",
                    "Gastos Financieros",
                    "DEUDORA"
            );
        }


        if (codigo.equals("2.2.01")) {

            return new GrupoMayor(
                    "2.2.01",
                    "Préstamo Bancario",
                    "ACREEDORA"
            );
        }


        return new GrupoMayor(
                codigo,
                cuenta.getNombre(),
                cuenta.getNaturaleza()
        );
    }


    private int ordenGrupo(
            String codigo
    ) {

        return switch (codigo) {

            case "1.1.01" -> 10;
            case "1.1.03" -> 20;
            case "3.1.01" -> 30;
            case "5.1.01" -> 40;
            case "1.1.04" -> 50;
            case "5.1.02" -> 60;
            case "2.1.01" -> 70;
            case "4.1.01" -> 80;
            case "4.1.02" -> 90;
            case "2.1.02" -> 100;
            case "1.1.02" -> 110;
            case "1.2.01" -> 120;
            case "6.3.01" -> 130;
            case "2.2.01" -> 140;

            default -> 1000;
        };
    }


    private void calcularSaldo(
            CuentaMayor cuenta
    ) {

        BigDecimal debe =
                nuloACero(
                        cuenta.getTotalDebe()
                );

        BigDecimal haber =
                nuloACero(
                        cuenta.getTotalHaber()
                );


        int comparacion =
                debe.compareTo(
                        haber
                );


        if (comparacion > 0) {

            cuenta.setSaldo(
                    debe.subtract(
                            haber
                    )
            );

            cuenta.setTipoSaldo(
                    "DEUDOR"
            );

        } else if (comparacion < 0) {

            cuenta.setSaldo(
                    haber.subtract(
                            debe
                    )
            );

            cuenta.setTipoSaldo(
                    "ACREEDOR"
            );

        } else {

            cuenta.setSaldo(
                    BigDecimal.ZERO
            );

            cuenta.setTipoSaldo(
                    "SALDADA"
            );
        }
    }


    private BigDecimal nuloACero(
            BigDecimal valor
    ) {

        return valor == null
                ? BigDecimal.ZERO
                : valor;
    }


    private static class GrupoMayor {

        private final String codigo;
        private final String nombre;
        private final String naturaleza;

        private GrupoMayor(
                String codigo,
                String nombre,
                String naturaleza
        ) {

            this.codigo = codigo;
            this.nombre = nombre;
            this.naturaleza = naturaleza;
        }
    }
}
