package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import java.awt.Color;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class GeneradorReportesService {
    public record Reporte(String titulo,String[] columnas,List<String[]> filas,String periodo){}
    private static final String[] TIPOS={"Balance General","Estado de Resultados","Balance de Comprobación","Libro Diario","Libro Mayor","Auxiliares","IVA"};

    public Reporte generar(String tipo){ PeriodoContable p=new PeriodoService().obtenerPeriodoActivo(); return generar(tipo,p.getIdPeriodo(),p.getNombre()); }
    public Reporte generar(String tipo,int idPeriodo,String nombrePeriodo){
        return switch(tipo){
            case "Balance General"->q(tipo,new String[]{"Código","Cuenta","Tipo","Clasificación","Saldo"},"SELECT cc.codigo,cc.nombre,cc.tipo,cc.clasificacion,ROUND(SUM(CASE WHEN cc.naturaleza='DEUDORA' THEN d.debe-d.haber ELSE d.haber-d.debe END),2) saldo FROM catalogo_cuentas cc JOIN detalle_asientos d ON d.id_cuenta=cc.id_cuenta JOIN asientos_contables a ON a.id_asiento=d.id_asiento WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? AND cc.tipo IN('ACTIVO','PASIVO','PATRIMONIO') GROUP BY cc.id_cuenta,cc.codigo,cc.nombre,cc.tipo,cc.clasificacion HAVING ABS(saldo)>0.004 ORDER BY cc.codigo",idPeriodo,nombrePeriodo);
            case "Estado de Resultados"->q(tipo,new String[]{"Código","Cuenta","Tipo","Clasificación","Saldo"},"SELECT cc.codigo,cc.nombre,cc.tipo,cc.clasificacion,ROUND(SUM(CASE WHEN cc.naturaleza='ACREEDORA' THEN d.haber-d.debe ELSE d.debe-d.haber END),2) saldo FROM catalogo_cuentas cc JOIN detalle_asientos d ON d.id_cuenta=cc.id_cuenta JOIN asientos_contables a ON a.id_asiento=d.id_asiento WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? AND cc.tipo IN('INGRESO','COSTO','GASTO') GROUP BY cc.id_cuenta,cc.codigo,cc.nombre,cc.tipo,cc.clasificacion ORDER BY cc.codigo",idPeriodo,nombrePeriodo);
            case "Balance de Comprobación"->q(tipo,new String[]{"Código","Cuenta","Debe","Haber","Saldo deudor","Saldo acreedor"},"SELECT cc.codigo,cc.nombre,ROUND(SUM(d.debe),2),ROUND(SUM(d.haber),2),ROUND(GREATEST(SUM(d.debe)-SUM(d.haber),0),2),ROUND(GREATEST(SUM(d.haber)-SUM(d.debe),0),2) FROM catalogo_cuentas cc JOIN detalle_asientos d ON d.id_cuenta=cc.id_cuenta JOIN asientos_contables a ON a.id_asiento=d.id_asiento WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? GROUP BY cc.id_cuenta,cc.codigo,cc.nombre ORDER BY cc.codigo",idPeriodo,nombrePeriodo);
            case "Libro Diario"->q(tipo,new String[]{"Asiento","Fecha","Concepto","Código","Cuenta","Debe","Haber"},"SELECT a.numero_asiento,a.fecha,a.concepto,cc.codigo,cc.nombre,d.debe,d.haber FROM asientos_contables a JOIN detalle_asientos d ON d.id_asiento=a.id_asiento JOIN catalogo_cuentas cc ON cc.id_cuenta=d.id_cuenta WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? ORDER BY a.fecha,a.numero_asiento,d.id_detalle",idPeriodo,nombrePeriodo);
            case "Libro Mayor"->q(tipo,new String[]{"Código","Cuenta","Fecha","Asiento","Concepto","Debe","Haber"},"SELECT cc.codigo,cc.nombre,a.fecha,a.numero_asiento,a.concepto,d.debe,d.haber FROM catalogo_cuentas cc JOIN detalle_asientos d ON d.id_cuenta=cc.id_cuenta JOIN asientos_contables a ON a.id_asiento=d.id_asiento WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? ORDER BY cc.codigo,a.fecha,a.numero_asiento",idPeriodo,nombrePeriodo);
            case "Auxiliares"->q(tipo,new String[]{"Código","Cuenta","Fecha","Detalle","Debe","Haber"},"SELECT cc.codigo,cc.nombre,a.fecha,COALESCE(d.descripcion,a.concepto),d.debe,d.haber FROM detalle_asientos d JOIN asientos_contables a ON a.id_asiento=d.id_asiento JOIN catalogo_cuentas cc ON cc.id_cuenta=d.id_cuenta WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? ORDER BY cc.codigo,a.fecha",idPeriodo,nombrePeriodo);
            case "IVA"->q(tipo,new String[]{"Código","Cuenta IVA","Debe","Haber","Saldo"},"SELECT cc.codigo,cc.nombre,ROUND(SUM(d.debe),2),ROUND(SUM(d.haber),2),ROUND(SUM(CASE WHEN cc.naturaleza='DEUDORA' THEN d.debe-d.haber ELSE d.haber-d.debe END),2) FROM detalle_asientos d JOIN asientos_contables a ON a.id_asiento=d.id_asiento JOIN catalogo_cuentas cc ON cc.id_cuenta=d.id_cuenta WHERE a.estado='CONTABILIZADO' AND a.id_periodo=? AND cc.rol_reporte IN('IVA_CREDITO','IVA_DEBITO') GROUP BY cc.id_cuenta,cc.codigo,cc.nombre,cc.naturaleza ORDER BY cc.codigo",idPeriodo,nombrePeriodo);
            default->throw new IllegalArgumentException("Reporte no soportado");
        };
    }

    private Reporte q(String t,String[] cols,String sql,int idPeriodo,String periodo){
        List<String[]> f=new ArrayList<>();
        try(Connection c=Conexion.conectar();PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,idPeriodo);try(ResultSet r=p.executeQuery()){int n=cols.length;while(r.next()){String[] a=new String[n];for(int i=0;i<n;i++)a[i]=Objects.toString(r.getObject(i+1),"");f.add(a);}}}
        catch(SQLException e){throw new RuntimeException("No se pudo generar "+t+": "+e.getMessage(),e);}return new Reporte(t,cols,f,periodo);
    }

    public void excel(Reporte r,File f)throws IOException{try(Workbook w=new XSSFWorkbook()){crearHoja(w,r,"Reporte");try(OutputStream o=new FileOutputStream(f)){w.write(o);}}}
    public void excelPeriodoCompleto(int idPeriodo,String nombre,File f)throws IOException{try(Workbook w=new XSSFWorkbook()){for(String t:TIPOS)crearHoja(w,generar(t,idPeriodo,nombre),nombreHoja(t));try(OutputStream o=new FileOutputStream(f)){w.write(o);}}}

    private void crearHoja(Workbook w,Reporte r,String nombre){
        Sheet s=w.createSheet(nombre.length()>31?nombre.substring(0,31):nombre);
        CellStyle titulo=w.createCellStyle();titulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());titulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);titulo.setAlignment(HorizontalAlignment.LEFT);org.apache.poi.ss.usermodel.Font ft=w.createFont();ft.setBold(true);ft.setFontHeightInPoints((short)16);ft.setColor(IndexedColors.WHITE.getIndex());titulo.setFont(ft);
        CellStyle sub=w.createCellStyle();org.apache.poi.ss.usermodel.Font fs=w.createFont();fs.setItalic(true);fs.setColor(IndexedColors.GREY_50_PERCENT.getIndex());sub.setFont(fs);
        CellStyle head=w.createCellStyle();head.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());head.setFillPattern(FillPatternType.SOLID_FOREGROUND);head.setBorderBottom(BorderStyle.THIN);head.setBorderTop(BorderStyle.THIN);head.setBorderLeft(BorderStyle.THIN);head.setBorderRight(BorderStyle.THIN);org.apache.poi.ss.usermodel.Font fh=w.createFont();fh.setBold(true);head.setFont(fh);
        CellStyle body=w.createCellStyle();body.setBorderBottom(BorderStyle.HAIR);body.setBorderLeft(BorderStyle.HAIR);body.setBorderRight(BorderStyle.HAIR);
        CellStyle money=w.createCellStyle();money.cloneStyleFrom(body);money.setDataFormat(w.createDataFormat().getFormat("$#,##0.00;[Red]-$#,##0.00"));
        int last=Math.max(0,r.columnas.length-1);Row tr=s.createRow(0);Cell tc=tr.createCell(0);tc.setCellValue("ContaProMax | "+r.titulo);tc.setCellStyle(titulo);s.addMergedRegion(new CellRangeAddress(0,0,0,last));tr.setHeightInPoints(26);
        Row sr=s.createRow(1);sr.createCell(0).setCellValue("Período: "+Objects.toString(r.periodo,"-")+"   |   Generado: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));sr.getCell(0).setCellStyle(sub);s.addMergedRegion(new CellRangeAddress(1,1,0,last));
        Row h=s.createRow(3);for(int i=0;i<r.columnas.length;i++){Cell c=h.createCell(i);c.setCellValue(r.columnas[i]);c.setCellStyle(head);}int rr=4;
        for(String[] x:r.filas){Row row=s.createRow(rr++);for(int i=0;i<x.length;i++){Cell c=row.createCell(i);String v=x[i];if(esMonetaria(r.columnas[i])&&esNumero(v)){c.setCellValue(Double.parseDouble(v));c.setCellStyle(money);}else{c.setCellValue(v);c.setCellStyle(body);}}}
        s.createFreezePane(0,4);s.setAutoFilter(new CellRangeAddress(3,Math.max(3,rr-1),0,last));for(int i=0;i<r.columnas.length;i++){s.autoSizeColumn(i);int ancho=Math.max(s.getColumnWidth(i)+800,3000);s.setColumnWidth(i,Math.min(ancho,15000));}
        s.setDisplayGridlines(false);
    }
    private boolean esMonetaria(String c){String x=c.toLowerCase();return x.contains("debe")||x.contains("haber")||x.contains("saldo")||x.contains("total")||x.contains("monto");}
    private boolean esNumero(String v){try{Double.parseDouble(v);return true;}catch(Exception e){return false;}}
    private String nombreHoja(String s){return s.replace("Balance de Comprobación","Balance Comprobacion").replace("Estado de Resultados","Estado Resultados");}

    public void pdf(Reporte r,File f)throws IOException{
        try(PDDocument d=new PDDocument()){
            PDType1Font font=new PDType1Font(Standard14Fonts.FontName.HELVETICA);PDType1Font bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);PDImageXObject logo=null;
            try(InputStream in=getClass().getResourceAsStream("/branding/contapromax-logo.png")){if(in!=null)logo=PDImageXObject.createFromByteArray(d,in.readAllBytes(),"ContaProMax");}catch(Exception ignored){}
            int start=0;while(start<Math.max(1,r.filas.size())){PDPage p=new PDPage(new PDRectangle(PDRectangle.A4.getHeight(),PDRectangle.A4.getWidth()));d.addPage(p);try(PDPageContentStream cs=new PDPageContentStream(d,p)){float y=555;if(logo!=null)cs.drawImage(logo,35,535,48,48);cs.setNonStrokingColor(new Color(30,64,175));cs.beginText();cs.setFont(bold,16);cs.newLineAtOffset(95,y);cs.showText("ContaProMax");cs.endText();cs.setNonStrokingColor(Color.DARK_GRAY);cs.beginText();cs.setFont(bold,13);cs.newLineAtOffset(95,y-20);cs.showText(lim(r.titulo,80));cs.endText();cs.beginText();cs.setFont(font,8);cs.newLineAtOffset(95,y-35);cs.showText("Periodo: "+lim(Objects.toString(r.periodo,"-"),45)+"  |  Generado: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));cs.endText();y-=70;float width=770f/r.columnas.length;cs.setNonStrokingColor(new Color(219,234,254));cs.addRect(35,y-4,770,17);cs.fill();cs.setNonStrokingColor(Color.BLACK);filaPdf(cs,bold,r.columnas,35,y,width);y-=19;int end=Math.min(start+23,r.filas.size());for(int j=start;j<end;j++){filaPdf(cs,font,r.filas.get(j),35,y,width);y-=18;}start=end;if(r.filas.isEmpty()){cs.beginText();cs.setFont(font,10);cs.newLineAtOffset(35,y);cs.showText("Sin movimientos para este periodo.");cs.endText();start=1;}}}d.save(f);
        }
    }
    private void filaPdf(PDPageContentStream cs,PDFont font,String[] vals,float x,float y,float w)throws IOException{for(int i=0;i<vals.length;i++){cs.beginText();cs.setFont(font,7);cs.newLineAtOffset(x+i*w,y);cs.showText(lim(Objects.toString(vals[i],""),(int)Math.max(8,w/4.2)));cs.endText();}}
    private String lim(String s,int n){s=s.replaceAll("[\\r\\n]"," ");return s.length()>n?s.substring(0,n-1)+"...":s;}
}
