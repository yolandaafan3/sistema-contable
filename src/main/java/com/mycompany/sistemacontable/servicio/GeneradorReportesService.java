package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class GeneradorReportesService {

    public record Reporte(
            String titulo,
            String[] columnas,
            List<String[]> filas,
            String periodo
    ) {}

    private static final String LOGO_RECURSO =
            "/branding/ContraProMaxLogo.png";

    private static final String[] TIPOS = {
        "Balance General",
        "Estado de Resultados",
        "Balance de Comprobación",
        "Libro Diario",
        "Libro Mayor",
        "Auxiliares",
        "IVA",
        "Kardex"
    };

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Color PDF_AZUL =
            new Color(15, 76, 151);

    private static final Color PDF_AZUL_CLARO =
            new Color(232, 242, 255);

    private static final Color PDF_AZUL_MUY_CLARO =
            new Color(248, 251, 255);

    private static final Color PDF_TEXTO =
            new Color(30, 41, 59);

    private static final Color PDF_SECUNDARIO =
            new Color(100, 116, 139);

    private static final Color PDF_BORDE =
            new Color(203, 213, 225);

    public Reporte generar(String tipo) {

        PeriodoContable periodo =
                new PeriodoService().obtenerPeriodoActivo();

        return generar(
                tipo,
                periodo.getIdPeriodo(),
                periodo.getNombre()
        );
    }

    public Reporte generar(
            String tipo,
            int idPeriodo,
            String nombrePeriodo
    ) {

        return switch (tipo) {

            case "Balance General" ->
                q(
                        tipo,
                        new String[]{
                            "Código",
                            "Cuenta",
                            "Tipo",
                            "Clasificación",
                            "Saldo"
                        },
                        """
                        SELECT
                            cc.codigo,
                            cc.nombre,
                            cc.tipo,
                            cc.clasificacion,
                            ROUND(
                                SUM(
                                    CASE
                                        WHEN cc.naturaleza = 'DEUDORA'
                                            THEN d.debe - d.haber
                                        ELSE d.haber - d.debe
                                    END
                                ),
                                2
                            ) AS saldo
                        FROM catalogo_cuentas cc
                        JOIN detalle_asientos d
                            ON d.id_cuenta = cc.id_cuenta
                        JOIN asientos_contables a
                            ON a.id_asiento = d.id_asiento
                        WHERE a.estado = 'CONTABILIZADO'
                          AND a.id_periodo = ?
                          AND cc.tipo IN ('ACTIVO','PASIVO','PATRIMONIO')
                        GROUP BY
                            cc.id_cuenta,
                            cc.codigo,
                            cc.nombre,
                            cc.tipo,
                            cc.clasificacion
                        HAVING ABS(saldo) > 0.004
                        ORDER BY cc.codigo
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "Estado de Resultados" ->
                q(
                        tipo,
                        new String[]{
                            "Código",
                            "Cuenta",
                            "Tipo",
                            "Clasificación",
                            "Saldo"
                        },
                        """
                        SELECT
                            cc.codigo,
                            cc.nombre,
                            cc.tipo,
                            cc.clasificacion,
                            ROUND(
                                SUM(
                                    CASE
                                        WHEN cc.naturaleza = 'ACREEDORA'
                                            THEN d.haber - d.debe
                                        ELSE d.debe - d.haber
                                    END
                                ),
                                2
                            ) AS saldo
                        FROM catalogo_cuentas cc
                        JOIN detalle_asientos d
                            ON d.id_cuenta = cc.id_cuenta
                        JOIN asientos_contables a
                            ON a.id_asiento = d.id_asiento
                        WHERE a.estado = 'CONTABILIZADO'
                          AND a.id_periodo = ?
                          AND cc.tipo IN ('INGRESO','COSTO','GASTO')
                        GROUP BY
                            cc.id_cuenta,
                            cc.codigo,
                            cc.nombre,
                            cc.tipo,
                            cc.clasificacion
                        ORDER BY cc.codigo
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "Balance de Comprobación" ->
                q(
                        tipo,
                        new String[]{
                            "Código",
                            "Cuenta",
                            "Debe",
                            "Haber",
                            "Saldo deudor",
                            "Saldo acreedor"
                        },
                        """
                        SELECT
                            cc.codigo,
                            cc.nombre,
                            ROUND(SUM(d.debe),2),
                            ROUND(SUM(d.haber),2),
                            ROUND(GREATEST(SUM(d.debe)-SUM(d.haber),0),2),
                            ROUND(GREATEST(SUM(d.haber)-SUM(d.debe),0),2)
                        FROM catalogo_cuentas cc
                        JOIN detalle_asientos d
                            ON d.id_cuenta=cc.id_cuenta
                        JOIN asientos_contables a
                            ON a.id_asiento=d.id_asiento
                        WHERE a.estado='CONTABILIZADO'
                          AND a.id_periodo=?
                        GROUP BY
                            cc.id_cuenta,
                            cc.codigo,
                            cc.nombre
                        ORDER BY cc.codigo
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "Libro Diario" ->
                q(
                        tipo,
                        new String[]{
                            "Asiento",
                            "Fecha",
                            "Concepto",
                            "Código",
                            "Cuenta",
                            "Debe",
                            "Haber"
                        },
                        """
                        SELECT
                            a.numero_asiento,
                            a.fecha,
                            a.concepto,
                            cc.codigo,
                            cc.nombre,
                            d.debe,
                            d.haber
                        FROM asientos_contables a
                        JOIN detalle_asientos d
                            ON d.id_asiento=a.id_asiento
                        JOIN catalogo_cuentas cc
                            ON cc.id_cuenta=d.id_cuenta
                        WHERE a.estado='CONTABILIZADO'
                          AND a.id_periodo=?
                        ORDER BY
                            a.fecha,
                            a.numero_asiento,
                            d.id_detalle
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "Libro Mayor" ->
                q(
                        tipo,
                        new String[]{
                            "Código",
                            "Cuenta",
                            "Fecha",
                            "Asiento",
                            "Concepto",
                            "Debe",
                            "Haber"
                        },
                        """
                        SELECT
                            cc.codigo,
                            cc.nombre,
                            a.fecha,
                            a.numero_asiento,
                            a.concepto,
                            d.debe,
                            d.haber
                        FROM catalogo_cuentas cc
                        JOIN detalle_asientos d
                            ON d.id_cuenta=cc.id_cuenta
                        JOIN asientos_contables a
                            ON a.id_asiento=d.id_asiento
                        WHERE a.estado='CONTABILIZADO'
                          AND a.id_periodo=?
                        ORDER BY
                            cc.codigo,
                            a.fecha,
                            a.numero_asiento
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "Auxiliares" ->
                q(
                        tipo,
                        new String[]{
                            "Código",
                            "Cuenta",
                            "Fecha",
                            "Detalle",
                            "Debe",
                            "Haber"
                        },
                        """
                        SELECT
                            cc.codigo,
                            cc.nombre,
                            a.fecha,
                            COALESCE(d.descripcion,a.concepto),
                            d.debe,
                            d.haber
                        FROM detalle_asientos d
                        JOIN asientos_contables a
                            ON a.id_asiento=d.id_asiento
                        JOIN catalogo_cuentas cc
                            ON cc.id_cuenta=d.id_cuenta
                        WHERE a.estado='CONTABILIZADO'
                          AND a.id_periodo=?
                        ORDER BY
                            cc.codigo,
                            a.fecha
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "IVA" ->
                q(
                        tipo,
                        new String[]{
                            "Código",
                            "Cuenta IVA",
                            "Debe",
                            "Haber",
                            "Saldo"
                        },
                        """
                        SELECT
                            cc.codigo,
                            cc.nombre,
                            ROUND(SUM(d.debe),2),
                            ROUND(SUM(d.haber),2),
                            ROUND(
                                SUM(
                                    CASE
                                        WHEN cc.naturaleza='DEUDORA'
                                            THEN d.debe-d.haber
                                        ELSE d.haber-d.debe
                                    END
                                ),
                                2
                            )
                        FROM detalle_asientos d
                        JOIN asientos_contables a
                            ON a.id_asiento=d.id_asiento
                        JOIN catalogo_cuentas cc
                            ON cc.id_cuenta=d.id_cuenta
                        WHERE a.estado='CONTABILIZADO'
                          AND a.id_periodo=?
                          AND cc.rol_reporte IN('IVA_CREDITO','IVA_DEBITO')
                        GROUP BY
                            cc.id_cuenta,
                            cc.codigo,
                            cc.nombre,
                            cc.naturaleza
                        ORDER BY cc.codigo
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            case "Kardex" ->
                q(
                        tipo,
                        new String[]{
                            "Producto",
                            "Fecha",
                            "Asiento",
                            "Concepto",
                            "Entrada",
                            "Salida",
                            "Existencia",
                            "Costo Unit.",
                            "Costo PEPS",
                            "Saldo Deudor",
                            "Saldo Acreedor",
                            "Saldo"
                        },
                        """
                        SELECT
                            CONCAT(p.codigo,' - ',p.nombre) AS producto,
                            k.fecha,
                            a.numero_asiento,
                            k.concepto,
                            ROUND(k.unidades_entrada,6),
                            ROUND(k.unidades_salida,6),
                            ROUND(k.unidades_existencia,6),
                            ROUND(k.costo_unitario,2),
                            ROUND(k.costo_peps,2),
                            ROUND(k.saldo_deudor,2),
                            ROUND(k.saldo_acreedor,2),
                            ROUND(k.saldo,2)
                        FROM kardex k
                        JOIN productos p
                            ON p.id_producto=k.id_producto
                        JOIN asientos_contables a
                            ON a.id_asiento=k.id_asiento
                        WHERE a.id_periodo=?
                          AND a.estado='CONTABILIZADO'
                        ORDER BY
                            k.fecha,
                            p.codigo,
                            k.id_kardex
                        """,
                        idPeriodo,
                        nombrePeriodo
                );

            default ->
                throw new IllegalArgumentException(
                        "Reporte no soportado: " + tipo
                );
        };
    }

    private Reporte q(
            String titulo,
            String[] columnas,
            String sql,
            int idPeriodo,
            String periodo
    ) {

        List<String[]> filas =
                new ArrayList<>();

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idPeriodo
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    String[] fila =
                            new String[
                                    columnas.length
                            ];

                    for (
                            int i = 0;
                            i < columnas.length;
                            i++
                    ) {

                        fila[i] =
                                Objects.toString(
                                        rs.getObject(i + 1),
                                        ""
                                );
                    }

                    filas.add(
                            fila
                    );
                }
            }

        } catch (
                SQLException e
        ) {

            throw new RuntimeException(
                    "No se pudo generar "
                    + titulo
                    + ": "
                    + e.getMessage(),
                    e
            );
        }

        return new Reporte(
                titulo,
                columnas,
                filas,
                periodo
        );
    }

    public void excel(
            Reporte reporte,
            File archivo
    ) throws IOException {

        try (
                Workbook workbook =
                        new XSSFWorkbook()
        ) {

            crearHoja(
                    workbook,
                    reporte,
                    "Reporte"
            );

            try (
                    OutputStream salida =
                            new FileOutputStream(
                                    archivo
                            )
            ) {

                workbook.write(
                        salida
                );
            }
        }
    }

    public void excelPeriodoCompleto(
            int idPeriodo,
            String nombre,
            File archivo
    ) throws IOException {

        try (
                Workbook workbook =
                        new XSSFWorkbook()
        ) {

            for (
                    String tipo
                    : TIPOS
            ) {

                crearHoja(
                        workbook,
                        generar(
                                tipo,
                                idPeriodo,
                                nombre
                        ),
                        nombreHoja(tipo)
                );
            }

            try (
                    OutputStream salida =
                            new FileOutputStream(
                                    archivo
                            )
            ) {

                workbook.write(
                        salida
                );
            }
        }
    }

    private void crearHoja(
            Workbook workbook,
            Reporte reporte,
            String nombre
    ) throws IOException {

        String nombreSeguro =
                nombre.length() > 31
                        ? nombre.substring(0, 31)
                        : nombre;

        Sheet hoja =
                workbook.createSheet(
                        nombreSeguro
                );

        hoja.setDisplayGridlines(
                false
        );

        hoja.setFitToPage(
                true
        );

        PrintSetup printSetup =
                hoja.getPrintSetup();

        printSetup.setFitWidth(
                (short) 1
        );

        printSetup.setFitHeight(
                (short) 0
        );

        printSetup.setLandscape(
                reporte.columnas().length > 5
        );

        int ultimaColumna =
                Math.max(
                        0,
                        reporte.columnas().length - 1
                );

        CellStyle estiloTitulo =
                crearEstiloTituloExcel(
                        workbook
                );

        CellStyle estiloSubtitulo =
                crearEstiloSubtituloExcel(
                        workbook
                );

        CellStyle estiloMeta =
                crearEstiloMetaExcel(
                        workbook
                );

        CellStyle estiloEncabezado =
                crearEstiloEncabezadoExcel(
                        workbook
                );

        CellStyle estiloPar =
                crearEstiloCuerpoExcel(
                        workbook,
                        false
                );

        CellStyle estiloImpar =
                crearEstiloCuerpoExcel(
                        workbook,
                        true
                );

        CellStyle estiloDineroPar =
                crearEstiloDineroExcel(
                        workbook,
                        estiloPar
                );

        CellStyle estiloDineroImpar =
                crearEstiloDineroExcel(
                        workbook,
                        estiloImpar
                );

        CellStyle estiloNumeroPar =
                crearEstiloNumeroExcel(
                        workbook,
                        estiloPar
                );

        CellStyle estiloNumeroImpar =
                crearEstiloNumeroExcel(
                        workbook,
                        estiloImpar
                );

        insertarLogoExcel(
                workbook,
                hoja
        );

        Row filaTitulo =
                hoja.createRow(
                        0
                );

        filaTitulo.setHeightInPoints(
                30
        );

        Cell titulo =
                filaTitulo.createCell(
                        2
                );

        titulo.setCellValue(
                "ContaProMax"
        );

        titulo.setCellStyle(
                estiloTitulo
        );

        if (
                ultimaColumna >= 2
        ) {

            hoja.addMergedRegion(
                    new CellRangeAddress(
                            0,
                            0,
                            2,
                            ultimaColumna
                    )
            );
        }

        Row filaNombreReporte =
                hoja.createRow(
                        1
                );

        Cell nombreReporte =
                filaNombreReporte.createCell(
                        2
                );

        nombreReporte.setCellValue(
                reporte.titulo()
        );

        nombreReporte.setCellStyle(
                estiloSubtitulo
        );

        if (
                ultimaColumna >= 2
        ) {

            hoja.addMergedRegion(
                    new CellRangeAddress(
                            1,
                            1,
                            2,
                            ultimaColumna
                    )
            );
        }

        Row filaMeta =
                hoja.createRow(
                        2
                );

        Cell meta =
                filaMeta.createCell(
                        2
                );

        meta.setCellValue(
                "Período: "
                + Objects.toString(
                        reporte.periodo(),
                        "-"
                )
                + "   |   Registros: "
                + reporte.filas().size()
                + "   |   Generado: "
                + LocalDateTime.now()
                        .format(
                                FORMATO_FECHA_HORA
                        )
        );

        meta.setCellStyle(
                estiloMeta
        );

        if (
                ultimaColumna >= 2
        ) {

            hoja.addMergedRegion(
                    new CellRangeAddress(
                            2,
                            2,
                            2,
                            ultimaColumna
                    )
            );
        }

        Row franja =
                hoja.createRow(
                        4
                );

        franja.setHeightInPoints(
                8
        );

        for (
                int i = 0;
                i <= ultimaColumna;
                i++
        ) {

            Cell celda =
                    franja.createCell(i);

            CellStyle estilo =
                    workbook.createCellStyle();

            estilo.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex()
            );

            estilo.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            celda.setCellStyle(
                    estilo
            );
        }

        int filaEncabezado =
                6;

        Row encabezado =
                hoja.createRow(
                        filaEncabezado
                );

        encabezado.setHeightInPoints(
                27
        );

        for (
                int i = 0;
                i < reporte.columnas().length;
                i++
        ) {

            Cell celda =
                    encabezado.createCell(
                            i
                    );

            celda.setCellValue(
                    reporte.columnas()[i]
            );

            celda.setCellStyle(
                    estiloEncabezado
            );
        }

        int filaActual =
                filaEncabezado + 1;

        for (
                int indiceFila = 0;
                indiceFila < reporte.filas().size();
                indiceFila++
        ) {

            String[] datos =
                    reporte.filas()
                            .get(indiceFila);

            Row fila =
                    hoja.createRow(
                            filaActual++
                    );

            fila.setHeightInPoints(
                    21
            );

            boolean impar =
                    indiceFila % 2 != 0;

            for (
                    int i = 0;
                    i < datos.length;
                    i++
            ) {

                Cell celda =
                        fila.createCell(i);

                String valor =
                        datos[i] == null
                                ? ""
                                : datos[i];

                String columna =
                        reporte.columnas()[i];

                if (
                        esMonetaria(columna)
                        &&
                        esNumero(valor)
                ) {

                    celda.setCellValue(
                            Double.parseDouble(
                                    valor
                            )
                    );

                    celda.setCellStyle(
                            impar
                                    ? estiloDineroImpar
                                    : estiloDineroPar
                    );

                } else if (
                        esCantidad(columna)
                        &&
                        esNumero(valor)
                ) {

                    celda.setCellValue(
                            Double.parseDouble(
                                    valor
                            )
                    );

                    celda.setCellStyle(
                            impar
                                    ? estiloNumeroImpar
                                    : estiloNumeroPar
                    );

                } else {

                    celda.setCellValue(
                            valor
                    );

                    celda.setCellStyle(
                            impar
                                    ? estiloImpar
                                    : estiloPar
                    );
                }
            }
        }

        if (
                reporte.filas().isEmpty()
        ) {

            Row filaVacia =
                    hoja.createRow(
                            filaActual++
                    );

            Cell mensaje =
                    filaVacia.createCell(
                            0
                    );

            mensaje.setCellValue(
                    "Sin datos para este período."
            );

            mensaje.setCellStyle(
                    estiloPar
            );

            hoja.addMergedRegion(
                    new CellRangeAddress(
                            filaVacia.getRowNum(),
                            filaVacia.getRowNum(),
                            0,
                            ultimaColumna
                    )
            );
        }

        hoja.createFreezePane(
                0,
                filaEncabezado + 1
        );

        hoja.setAutoFilter(
                new CellRangeAddress(
                        filaEncabezado,
                        Math.max(
                                filaEncabezado,
                                filaActual - 1
                        ),
                        0,
                        ultimaColumna
                )
        );

        for (
                int i = 0;
                i < reporte.columnas().length;
                i++
        ) {

            hoja.autoSizeColumn(i);

            int ancho =
                    Math.max(
                            hoja.getColumnWidth(i) + 900,
                            3200
                    );

            hoja.setColumnWidth(
                    i,
                    Math.min(
                            ancho,
                            16000
                    )
            );
        }

        hoja.setMargin(
                Sheet.LeftMargin,
                0.25
        );

        hoja.setMargin(
                Sheet.RightMargin,
                0.25
        );

        hoja.setMargin(
                Sheet.TopMargin,
                0.35
        );

        hoja.setMargin(
                Sheet.BottomMargin,
                0.35
        );

        Footer footer =
                hoja.getFooter();

        footer.setLeft(
                "ContaProMax · Reporte contable"
        );

        footer.setCenter(
                reporte.titulo()
        );

        footer.setRight(
                "Página &P de &N"
        );
    }

    private CellStyle crearEstiloTituloExcel(
            Workbook workbook
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        org.apache.poi.ss.usermodel.Font fuente =
                workbook.createFont();

        fuente.setBold(
                true
        );

        fuente.setFontHeightInPoints(
                (short) 20
        );

        fuente.setColor(
                IndexedColors.DARK_BLUE.getIndex()
        );

        estilo.setFont(
                fuente
        );

        estilo.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        return estilo;
    }

    private CellStyle crearEstiloSubtituloExcel(
            Workbook workbook
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        org.apache.poi.ss.usermodel.Font fuente =
                workbook.createFont();

        fuente.setBold(
                true
        );

        fuente.setFontHeightInPoints(
                (short) 14
        );

        fuente.setColor(
                IndexedColors.BLACK.getIndex()
        );

        estilo.setFont(
                fuente
        );

        return estilo;
    }

    private CellStyle crearEstiloMetaExcel(
            Workbook workbook
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        org.apache.poi.ss.usermodel.Font fuente =
                workbook.createFont();

        fuente.setItalic(
                true
        );

        fuente.setFontHeightInPoints(
                (short) 10
        );

        fuente.setColor(
                IndexedColors.GREY_50_PERCENT.getIndex()
        );

        estilo.setFont(
                fuente
        );

        return estilo;
    }

    private CellStyle crearEstiloEncabezadoExcel(
            Workbook workbook
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        estilo.setFillForegroundColor(
                IndexedColors.DARK_BLUE.getIndex()
        );

        estilo.setFillPattern(
                FillPatternType.SOLID_FOREGROUND
        );

        estilo.setAlignment(
                HorizontalAlignment.CENTER
        );

        estilo.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        estilo.setBorderBottom(
                BorderStyle.THIN
        );

        estilo.setBorderTop(
                BorderStyle.THIN
        );

        estilo.setBorderLeft(
                BorderStyle.THIN
        );

        estilo.setBorderRight(
                BorderStyle.THIN
        );

        org.apache.poi.ss.usermodel.Font fuente =
                workbook.createFont();

        fuente.setBold(
                true
        );

        fuente.setColor(
                IndexedColors.WHITE.getIndex()
        );

        estilo.setFont(
                fuente
        );

        estilo.setWrapText(
                true
        );

        return estilo;
    }

    private CellStyle crearEstiloCuerpoExcel(
            Workbook workbook,
            boolean alterno
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        estilo.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        estilo.setBorderBottom(
                BorderStyle.THIN
        );

        estilo.setBorderTop(
                BorderStyle.THIN
        );

        estilo.setBorderLeft(
                BorderStyle.THIN
        );

        estilo.setBorderRight(
                BorderStyle.THIN
        );

        estilo.setWrapText(
                false
        );

        if (
                alterno
        ) {

            estilo.setFillForegroundColor(
                    IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex()
            );

            estilo.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );
        }

        return estilo;
    }

    private CellStyle crearEstiloDineroExcel(
            Workbook workbook,
            CellStyle base
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        estilo.cloneStyleFrom(
                base
        );

        DataFormat formato =
                workbook.createDataFormat();

        estilo.setDataFormat(
                formato.getFormat(
                        "$#,##0.00;[Red]-$#,##0.00"
                )
        );

        estilo.setAlignment(
                HorizontalAlignment.RIGHT
        );

        return estilo;
    }

    private CellStyle crearEstiloNumeroExcel(
            Workbook workbook,
            CellStyle base
    ) {

        CellStyle estilo =
                workbook.createCellStyle();

        estilo.cloneStyleFrom(
                base
        );

        DataFormat formato =
                workbook.createDataFormat();

        estilo.setDataFormat(
                formato.getFormat(
                        "#,##0.######"
                )
        );

        estilo.setAlignment(
                HorizontalAlignment.RIGHT
        );

        return estilo;
    }

    private void insertarLogoExcel(
            Workbook workbook,
            Sheet hoja
    ) {

        try (
                InputStream input =
                        getClass()
                                .getResourceAsStream(
                                        LOGO_RECURSO
                                )
        ) {

            if (
                    input == null
            ) {

                return;
            }

            int indiceImagen =
                    workbook.addPicture(
                            input.readAllBytes(),
                            Workbook.PICTURE_TYPE_PNG
                    );

            CreationHelper helper =
                    workbook.getCreationHelper();

            Drawing<?> drawing =
                    hoja.createDrawingPatriarch();

            ClientAnchor anchor =
                    helper.createClientAnchor();

            anchor.setCol1(
                    0
            );

            anchor.setRow1(
                    0
            );

            anchor.setCol2(
                    2
            );

            anchor.setRow2(
                    3
            );

            Picture picture =
                    drawing.createPicture(
                            anchor,
                            indiceImagen
                    );

        } catch (
                Exception ignored
        ) {
        }
    }

    private boolean esMonetaria(
            String columna
    ) {

        String texto =
                columna.toLowerCase();

        return texto.contains("debe")
                || texto.contains("haber")
                || texto.contains("saldo")
                || texto.contains("total")
                || texto.contains("monto")
                || texto.contains("costo")
                || texto.contains("valor")
                || texto.contains("precio");
    }

    private boolean esCantidad(
            String columna
    ) {

        String texto =
                columna.toLowerCase();

        return texto.contains("entrada")
                || texto.contains("salida")
                || texto.contains("existencia")
                || texto.contains("cantidad")
                || texto.contains("consumido")
                || texto.contains("unidades");
    }

    private boolean esNumero(
            String valor
    ) {

        try {

            Double.parseDouble(
                    valor
            );

            return true;

        } catch (
                Exception e
        ) {

            return false;
        }
    }

    private String nombreHoja(
            String nombre
    ) {

        return nombre
                .replace(
                        "Balance de Comprobación",
                        "Balance Comprobacion"
                )
                .replace(
                        "Estado de Resultados",
                        "Estado Resultados"
                );
    }

    public void pdf(
            Reporte reporte,
            File archivo
    ) throws IOException {

        try (
                PDDocument documento =
                        new PDDocument()
        ) {

            PDFont fuente =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    );

            PDFont negrita =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    );

            PDImageXObject logo =
                    cargarLogoPdf(
                            documento
                    );

            PDRectangle formatoPagina =
                    reporte.columnas().length > 5
                            ? new PDRectangle(
                                    PDRectangle.A4.getHeight(),
                                    PDRectangle.A4.getWidth()
                            )
                            : PDRectangle.A4;

            float margen =
                    34f;

            float anchoDisponible =
                    formatoPagina.getWidth()
                    - margen * 2;

            float[] anchos =
                    calcularAnchosColumnas(
                            reporte,
                            anchoDisponible
                    );

            int indice =
                    0;

            int numeroPagina =
                    1;

            boolean sinDatos =
                    reporte.filas()
                            .isEmpty();

            do {

                PDPage pagina =
                        new PDPage(
                                formatoPagina
                        );

                documento.addPage(
                        pagina
                );

                try (
                        PDPageContentStream cs =
                                new PDPageContentStream(
                                        documento,
                                        pagina
                                )
                ) {

                    float y =
                            dibujarEncabezadoPdf(
                                    cs,
                                    pagina,
                                    reporte,
                                    logo,
                                    fuente,
                                    negrita,
                                    margen
                            );

                    y =
                            dibujarResumenPdf(
                                    cs,
                                    reporte,
                                    pagina,
                                    y,
                                    fuente,
                                    negrita,
                                    margen
                            );

                    y =
                            dibujarCabeceraTablaPdf(
                                    cs,
                                    reporte.columnas(),
                                    anchos,
                                    margen,
                                    y,
                                    negrita
                            );

                    if (
                            sinDatos
                    ) {

                        escribirTextoPdf(
                                cs,
                                fuente,
                                9,
                                PDF_SECUNDARIO,
                                margen + 6,
                                y - 20,
                                "Sin datos para este período."
                        );

                        indice =
                                1;

                    } else {

                        int filaVisual =
                                0;

                        while (
                                indice
                                < reporte.filas()
                                        .size()
                        ) {

                            if (
                                    y < 58
                            ) {

                                break;
                            }

                            y =
                                    dibujarFilaPdf(
                                            cs,
                                            reporte.columnas(),
                                            reporte.filas().get(indice),
                                            anchos,
                                            margen,
                                            y,
                                            fuente,
                                            filaVisual % 2 != 0
                                    );

                            indice++;

                            filaVisual++;
                        }
                    }

                    dibujarPiePdf(
                            cs,
                            pagina,
                            reporte,
                            fuente,
                            numeroPagina
                    );
                }

                numeroPagina++;

            } while (
                    indice
                    < Math.max(
                            1,
                            reporte.filas()
                                    .size()
                    )
            );

            documento.save(
                    archivo
            );
        }
    }

    private PDImageXObject cargarLogoPdf(
            PDDocument documento
    ) {

        try (
                InputStream input =
                        getClass()
                                .getResourceAsStream(
                                        LOGO_RECURSO
                                )
        ) {

            if (
                    input == null
            ) {

                return null;
            }

            return PDImageXObject.createFromByteArray(
                    documento,
                    input.readAllBytes(),
                    "ContraProMaxLogo"
            );

        } catch (
                Exception e
        ) {

            return null;
        }
    }

    private float dibujarEncabezadoPdf(
            PDPageContentStream cs,
            PDPage pagina,
            Reporte reporte,
            PDImageXObject logo,
            PDFont fuente,
            PDFont negrita,
            float margen
    ) throws IOException {

        float altoPagina =
                pagina.getMediaBox()
                        .getHeight();

        float anchoPagina =
                pagina.getMediaBox()
                        .getWidth();

        float ySuperior =
                altoPagina - margen;

        cs.setNonStrokingColor(
                PDF_AZUL
        );

        cs.addRect(
                margen,
                ySuperior + 7,
                anchoPagina - margen * 2,
                5
        );

        cs.fill();

        if (
                logo != null
        ) {

            float anchoLogo =
                    66f;

            float proporcion =
                    logo.getHeight() == 0
                            ? 1f
                            : (
                                    (float) logo.getWidth()
                                    / (float) logo.getHeight()
                            );

            float altoLogo =
                    anchoLogo
                    / proporcion;

            if (
                    altoLogo > 46
            ) {

                altoLogo =
                        46;

                anchoLogo =
                        altoLogo
                        * proporcion;
            }

            cs.drawImage(
                    logo,
                    margen,
                    ySuperior - altoLogo - 4,
                    anchoLogo,
                    altoLogo
            );
        }

        float textoX =
                margen
                + (
                        logo == null
                                ? 0
                                : 82
                );

        escribirTextoPdf(
                cs,
                negrita,
                18,
                PDF_AZUL,
                textoX,
                ySuperior - 15,
                "ContaProMax"
        );

        escribirTextoPdf(
                cs,
                negrita,
                14,
                PDF_TEXTO,
                textoX,
                ySuperior - 36,
                ajustarTexto(
                        reporte.titulo(),
                        negrita,
                        14,
                        anchoPagina - textoX - margen
                )
        );

        escribirTextoPdf(
                cs,
                fuente,
                8.5f,
                PDF_SECUNDARIO,
                textoX,
                ySuperior - 53,
                "Documento contable generado por el sistema"
        );

        cs.setStrokingColor(
                PDF_BORDE
        );

        cs.setLineWidth(
                0.6f
        );

        cs.moveTo(
                margen,
                ySuperior - 66
        );

        cs.lineTo(
                anchoPagina - margen,
                ySuperior - 66
        );

        cs.stroke();

        return ySuperior - 84;
    }

    private float dibujarResumenPdf(
            PDPageContentStream cs,
            Reporte reporte,
            PDPage pagina,
            float y,
            PDFont fuente,
            PDFont negrita,
            float margen
    ) throws IOException {

        float anchoPagina =
                pagina.getMediaBox()
                        .getWidth();

        float ancho =
                anchoPagina
                - margen * 2;

        float alto =
                31f;

        cs.setNonStrokingColor(
                PDF_AZUL_CLARO
        );

        cs.addRect(
                margen,
                y - alto,
                ancho,
                alto
        );

        cs.fill();

        escribirTextoPdf(
                cs,
                negrita,
                8.5f,
                PDF_AZUL,
                margen + 10,
                y - 12,
                "Período"
        );

        escribirTextoPdf(
                cs,
                fuente,
                8.5f,
                PDF_TEXTO,
                margen + 10,
                y - 24,
                Objects.toString(
                        reporte.periodo(),
                        "-"
                )
        );

        float xRegistros =
                margen + ancho * 0.38f;

        escribirTextoPdf(
                cs,
                negrita,
                8.5f,
                PDF_AZUL,
                xRegistros,
                y - 12,
                "Registros"
        );

        escribirTextoPdf(
                cs,
                fuente,
                8.5f,
                PDF_TEXTO,
                xRegistros,
                y - 24,
                String.valueOf(
                        reporte.filas().size()
                )
        );

        float xGenerado =
                margen + ancho * 0.62f;

        escribirTextoPdf(
                cs,
                negrita,
                8.5f,
                PDF_AZUL,
                xGenerado,
                y - 12,
                "Generado"
        );

        escribirTextoPdf(
                cs,
                fuente,
                8.5f,
                PDF_TEXTO,
                xGenerado,
                y - 24,
                LocalDateTime.now()
                        .format(
                                FORMATO_FECHA_HORA
                        )
        );

        return y - alto - 15;
    }

    private float dibujarCabeceraTablaPdf(
            PDPageContentStream cs,
            String[] columnas,
            float[] anchos,
            float x,
            float y,
            PDFont negrita
    ) throws IOException {

        float altoFila =
                24f;

        float total =
                sumar(
                        anchos
                );

        cs.setNonStrokingColor(
                PDF_AZUL
        );

        cs.addRect(
                x,
                y - altoFila,
                total,
                altoFila
        );

        cs.fill();

        float cursorX =
                x;

        for (
                int i = 0;
                i < columnas.length;
                i++
        ) {

            String texto =
                    ajustarTexto(
                            columnas[i],
                            negrita,
                            7.3f,
                            anchos[i] - 8
                    );

            escribirTextoPdf(
                    cs,
                    negrita,
                    7.3f,
                    Color.WHITE,
                    cursorX + 4,
                    y - 15,
                    texto
            );

            cursorX +=
                    anchos[i];
        }

        return y - altoFila;
    }

    private float dibujarFilaPdf(
            PDPageContentStream cs,
            String[] columnas,
            String[] fila,
            float[] anchos,
            float x,
            float y,
            PDFont fuente,
            boolean alterna
    ) throws IOException {

        float altoFila =
                21f;

        float total =
                sumar(
                        anchos
                );

        cs.setNonStrokingColor(
                alterna
                        ? PDF_AZUL_MUY_CLARO
                        : Color.WHITE
        );

        cs.addRect(
                x,
                y - altoFila,
                total,
                altoFila
        );

        cs.fill();

        float cursorX =
                x;

        for (
                int i = 0;
                i < anchos.length;
                i++
        ) {

            dibujarBordeCeldaPdf(
                    cs,
                    cursorX,
                    y - altoFila,
                    anchos[i],
                    altoFila
            );

            String valor =
                    i < fila.length
                            ? Objects.toString(
                                    fila[i],
                                    ""
                            )
                            : "";

            String mostrado =
                    formatearValorReporte(
                            columnas[i],
                            valor
                    );

            mostrado =
                    ajustarTexto(
                            mostrado,
                            fuente,
                            7.1f,
                            anchos[i] - 8
                    );

            boolean derecha =
                    esMonetaria(
                            columnas[i]
                    )
                    ||
                    esCantidad(
                            columnas[i]
                    );

            float textoX =
                    cursorX + 4;

            if (
                    derecha
            ) {

                textoX =
                        cursorX
                        + anchos[i]
                        - anchoTexto(
                                fuente,
                                7.1f,
                                mostrado
                        )
                        - 4;
            }

            escribirTextoPdf(
                    cs,
                    fuente,
                    7.1f,
                    PDF_TEXTO,
                    textoX,
                    y - 14,
                    mostrado
            );

            cursorX +=
                    anchos[i];
        }

        return y - altoFila;
    }

    private void dibujarBordeCeldaPdf(
            PDPageContentStream cs,
            float x,
            float y,
            float ancho,
            float alto
    ) throws IOException {

        cs.setStrokingColor(
                PDF_BORDE
        );

        cs.setLineWidth(
                0.4f
        );

        cs.addRect(
                x,
                y,
                ancho,
                alto
        );

        cs.stroke();
    }

    private void dibujarPiePdf(
            PDPageContentStream cs,
            PDPage pagina,
            Reporte reporte,
            PDFont fuente,
            int numeroPagina
    ) throws IOException {

        float margen =
                34f;

        float y =
                22f;

        cs.setStrokingColor(
                PDF_BORDE
        );

        cs.moveTo(
                margen,
                y + 10
        );

        cs.lineTo(
                pagina.getMediaBox()
                        .getWidth()
                        - margen,
                y + 10
        );

        cs.stroke();

        escribirTextoPdf(
                cs,
                fuente,
                7.5f,
                PDF_SECUNDARIO,
                margen,
                y,
                "ContaProMax · "
                + limpiarTextoPdf(
                        reporte.titulo()
                )
        );

        String paginaTexto =
                "Página "
                + numeroPagina;

        escribirTextoPdf(
                cs,
                fuente,
                7.5f,
                PDF_SECUNDARIO,
                pagina.getMediaBox()
                        .getWidth()
                        - margen
                        - anchoTexto(
                                fuente,
                                7.5f,
                                paginaTexto
                        ),
                y,
                paginaTexto
        );
    }

    private float[] calcularAnchosColumnas(
            Reporte reporte,
            float anchoDisponible
    ) {

        int cantidad =
                reporte.columnas().length;

        float[] pesos =
                new float[cantidad];

        float totalPesos =
                0f;

        for (
                int i = 0;
                i < cantidad;
                i++
        ) {

            int max =
                    reporte.columnas()[i]
                            .length();

            int limite =
                    Math.min(
                            reporte.filas().size(),
                            60
                    );

            for (
                    int fila = 0;
                    fila < limite;
                    fila++
            ) {

                String[] valores =
                        reporte.filas()
                                .get(fila);

                if (
                        i < valores.length
                        &&
                        valores[i] != null
                ) {

                    max =
                            Math.max(
                                    max,
                                    valores[i].length()
                            );
                }
            }

            float peso =
                    Math.max(
                            6f,
                            Math.min(
                                    max,
                                    28
                            )
                    );

            if (
                    esMonetaria(
                            reporte.columnas()[i]
                    )
                    ||
                    esCantidad(
                            reporte.columnas()[i]
                    )
            ) {

                peso =
                        Math.max(
                                peso,
                                10f
                        );
            }

            pesos[i] =
                    peso;

            totalPesos +=
                    peso;
        }

        float[] resultado =
                new float[cantidad];

        for (
                int i = 0;
                i < cantidad;
                i++
        ) {

            resultado[i] =
                    anchoDisponible
                    * (
                            pesos[i]
                            / totalPesos
                    );
        }

        return resultado;
    }

    private float sumar(
            float[] valores
    ) {

        float total =
                0f;

        for (
                float valor
                : valores
        ) {

            total +=
                    valor;
        }

        return total;
    }

    private String formatearValorReporte(
            String columna,
            String valor
    ) {

        if (
                valor == null
                ||
                valor.isBlank()
        ) {

            return "";
        }

        if (
                esMonetaria(columna)
                &&
                esNumero(valor)
        ) {

            try {

                return "$"
                        + String.format(
                                "%,.2f",
                                Double.parseDouble(valor)
                        );

            } catch (
                Exception ignored
            ) {
            }
        }

        if (
                esCantidad(columna)
                &&
                esNumero(valor)
        ) {

            try {

                return new BigDecimal(valor)
                        .setScale(
                                6,
                                RoundingMode.HALF_UP
                        )
                        .stripTrailingZeros()
                        .toPlainString();

            } catch (
                Exception ignored
            ) {
            }
        }

        return limpiarTextoPdf(
                valor
        );
    }

    private void escribirTextoPdf(
            PDPageContentStream cs,
            PDFont fuente,
            float tamano,
            Color color,
            float x,
            float y,
            String texto
    ) throws IOException {

        cs.setNonStrokingColor(
                color
        );

        cs.beginText();

        cs.setFont(
                fuente,
                tamano
        );

        cs.newLineAtOffset(
                x,
                y
        );

        cs.showText(
                limpiarTextoPdf(texto)
        );

        cs.endText();
    }

    private String ajustarTexto(
            String texto,
            PDFont fuente,
            float tamano,
            float anchoMaximo
    ) {

        String limpio =
                limpiarTextoPdf(
                        texto
                );

        if (
                limpio.isBlank()
        ) {

            return "";
        }

        if (
                anchoTexto(
                        fuente,
                        tamano,
                        limpio
                )
                <= anchoMaximo
        ) {

            return limpio;
        }

        String sufijo =
                "...";

        String actual =
                limpio;

        while (
                actual.length() > 1
                &&
                anchoTexto(
                        fuente,
                        tamano,
                        actual + sufijo
                )
                > anchoMaximo
        ) {

            actual =
                    actual.substring(
                            0,
                            actual.length() - 1
                    );
        }

        return actual + sufijo;
    }

    private float anchoTexto(
            PDFont fuente,
            float tamano,
            String texto
    ) {

        try {

            return fuente.getStringWidth(
                    limpiarTextoPdf(texto)
            )
            / 1000f
            * tamano;

        } catch (
                Exception e
        ) {

            return 0f;
        }
    }

    private String limpiarTextoPdf(
            String texto
    ) {

        if (
                texto == null
        ) {

            return "";
        }

        return texto
                .replace("\r", " ")
                .replace("\n", " ")
                .replace("\t", " ")
                .replace("–", "-")
                .replace("—", "-")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("’", "'");
    }
}
