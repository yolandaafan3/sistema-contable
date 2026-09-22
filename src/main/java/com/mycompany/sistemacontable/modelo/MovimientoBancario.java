package com.mycompany.sistemacontable.modelo;
import java.math.BigDecimal; import java.time.LocalDate;
public class MovimientoBancario {
 private long idMovimiento; private LocalDate fecha; private String descripcion, referencia, estado; private BigDecimal monto; private Integer idDetalleAsiento; private int puntaje; private String asientoSugerido;
 public long getIdMovimiento(){return idMovimiento;} public void setIdMovimiento(long v){idMovimiento=v;}
 public LocalDate getFecha(){return fecha;} public void setFecha(LocalDate v){fecha=v;}
 public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;}
 public String getReferencia(){return referencia;} public void setReferencia(String v){referencia=v;}
 public BigDecimal getMonto(){return monto;} public void setMonto(BigDecimal v){monto=v;}
 public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
 public Integer getIdDetalleAsiento(){return idDetalleAsiento;} public void setIdDetalleAsiento(Integer v){idDetalleAsiento=v;}
 public int getPuntaje(){return puntaje;} public void setPuntaje(int v){puntaje=v;}
 public String getAsientoSugerido(){return asientoSugerido;} public void setAsientoSugerido(String v){asientoSugerido=v;}
}
