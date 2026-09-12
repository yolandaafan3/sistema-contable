package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.modelo.BalanceComprobacion;
import com.mycompany.sistemacontable.modelo.CuentaMayor;
import com.mycompany.sistemacontable.modelo.LineaBalanceComprobacion;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class BalanceComprobacionService {

    private final MayorizacionService mayorizacionService;

    public BalanceComprobacionService() {

        mayorizacionService =
                new MayorizacionService();
    }


    public BalanceComprobacion generarBalance() {

        List<CuentaMayor> cuentas =
                mayorizacionService.obtenerMayorizacion();


        List<LineaBalanceComprobacion> lineas =
                new ArrayList<>();


        BigDecimal totalMovimientoDebe =
                BigDecimal.ZERO;

        BigDecimal totalMovimientoHaber =
                BigDecimal.ZERO;

        BigDecimal totalSaldoDeudor =
                BigDecimal.ZERO;

        BigDecimal totalSaldoAcreedor =
                BigDecimal.ZERO;


        for (CuentaMayor cuenta : cuentas) {

            LineaBalanceComprobacion linea =
                    new LineaBalanceComprobacion();


            linea.setIdCuenta(
                    cuenta.getIdCuenta()
            );

            linea.setCodigo(
                    cuenta.getCodigo()
            );

            linea.setNombre(
                    cuenta.getNombre()
            );


            linea.setMovimientoDebe(
                    cuenta.getTotalDebe()
            );

            linea.setMovimientoHaber(
                    cuenta.getTotalHaber()
            );


            if ("DEUDOR".equals(
                    cuenta.getTipoSaldo()
            )) {

                linea.setSaldoDeudor(
                        cuenta.getSaldo()
                );

                linea.setSaldoAcreedor(
                        BigDecimal.ZERO
                );

            } else if ("ACREEDOR".equals(
                    cuenta.getTipoSaldo()
            )) {

                linea.setSaldoDeudor(
                        BigDecimal.ZERO
                );

                linea.setSaldoAcreedor(
                        cuenta.getSaldo()
                );

            } else {

                linea.setSaldoDeudor(
                        BigDecimal.ZERO
                );

                linea.setSaldoAcreedor(
                        BigDecimal.ZERO
                );
            }


            totalMovimientoDebe =
                    totalMovimientoDebe.add(
                            cuenta.getTotalDebe()
                    );


            totalMovimientoHaber =
                    totalMovimientoHaber.add(
                            cuenta.getTotalHaber()
                    );


            totalSaldoDeudor =
                    totalSaldoDeudor.add(
                            linea.getSaldoDeudor()
                    );


            totalSaldoAcreedor =
                    totalSaldoAcreedor.add(
                            linea.getSaldoAcreedor()
                    );


            lineas.add(linea);
        }


        BalanceComprobacion balance =
                new BalanceComprobacion();


        balance.setLineas(
                lineas
        );


        balance.setTotalMovimientoDebe(
                totalMovimientoDebe
        );

        balance.setTotalMovimientoHaber(
                totalMovimientoHaber
        );


        balance.setTotalSaldoDeudor(
                totalSaldoDeudor
        );

        balance.setTotalSaldoAcreedor(
                totalSaldoAcreedor
        );


        balance.setMovimientosCuadrados(
                totalMovimientoDebe.compareTo(
                        totalMovimientoHaber
                ) == 0
        );


        balance.setSaldosCuadrados(
                totalSaldoDeudor.compareTo(
                        totalSaldoAcreedor
                ) == 0
        );


        return balance;
    }
}