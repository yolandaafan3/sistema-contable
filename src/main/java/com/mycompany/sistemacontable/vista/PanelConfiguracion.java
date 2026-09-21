package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.ConfiguracionContable;
import com.mycompany.sistemacontable.modelo.Empresa;
import com.mycompany.sistemacontable.servicio.ConfiguracionService;
import com.mycompany.sistemacontable.servicio.EmpresaService;
import com.mycompany.sistemacontable.servicio.ReinicioSistemaService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class PanelConfiguracion extends JPanel {

    private final ReinicioSistemaService reinicioService;
    private final ConfiguracionService configuracionService;
    private final EmpresaService empresaService;

    private JTextField txtNombreEmpresa;
    private JTextField txtNit;
    private JTextField txtNrc;
    private JTextField txtGiroComercial;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    private JLabel lblConfiguracionActual;
    private JTextField txtPorcentajeIva;
    private JComboBox<String> cmbTipoIva;

    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_TEXTO = new Color(30, 41, 59);
    private final Color COLOR_SECUNDARIO = new Color(100, 116, 139);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_ROJO = new Color(220, 38, 38);
    private final Color COLOR_ROJO_HOVER = new Color(185, 28, 28);

    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public PanelConfiguracion() {

        reinicioService = new ReinicioSistemaService();
        configuracionService = new ConfiguracionService();
        empresaService = new EmpresaService();

        configurarPanel();
        construirInterfaz();
        cargarConfiguracion();
        cargarEmpresa();
    }

    private void configurarPanel() {

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);

        setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );
    }

    private void construirInterfaz() {

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo = new JLabel("Configuración");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Administra la información de la empresa y los parámetros contables del sistema."
        );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(COLOR_SECUNDARIO);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(5));
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(20));

        // Zona de pruebas visible al inicio para que el administrador pueda
        // limpiar movimientos sin buscarla al final del scroll.
        contenido.add(crearTarjetaReinicio());
        contenido.add(Box.createVerticalStrut(20));

        contenido.add(crearTarjetaEmpresa());
        contenido.add(Box.createVerticalStrut(20));

        contenido.add(crearTarjetaIva());
        contenido.add(Box.createVerticalStrut(25));

        JScrollPane scroll = new JScrollPane(contenido);

        scroll.setBorder(null);
        scroll.setOpaque(false);

        scroll.getViewport().setOpaque(false);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // DATOS DE LA EMPRESA
    // =========================================================

    private JPanel crearTarjetaEmpresa() {

        JPanel tarjeta = new JPanel();

        tarjeta.setBackground(Color.WHITE);

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE),
                        BorderFactory.createEmptyBorder(
                                22,
                                22,
                                22,
                                22
                        )
                )
        );

        tarjeta.setMaximumSize(
        new Dimension(
                Integer.MAX_VALUE,
                500
        )
);

        tarjeta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = new JLabel(
                "Datos de la Empresa"
        );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(COLOR_TEXTO);

        JLabel descripcion = new JLabel(
                "Información general utilizada para identificar la empresa y generar documentos."
        );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        descripcion.setForeground(COLOR_SECUNDARIO);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(descripcion);
        tarjeta.add(Box.createVerticalStrut(20));

        txtNombreEmpresa = crearCampoTexto();
        txtNit = crearCampoTexto();
        txtNrc = crearCampoTexto();
        txtGiroComercial = crearCampoTexto();
        txtDireccion = crearCampoTexto();
        txtTelefono = crearCampoTexto();
        txtCorreo = crearCampoTexto();

        configurarCampoNit();
        configurarCampoNrc();
        configurarCampoTelefono();

        JPanel fila1 = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        18,
                        0
                )
        );

        fila1.setOpaque(false);

        fila1.add(
                crearGrupoCampo(
                        "Nombre / Razón social",
                        txtNombreEmpresa,
                        360
                )
        );

        fila1.add(
                crearGrupoCampo(
                        "NIT",
                        txtNit,
                        220
                )
        );

        fila1.add(
                crearGrupoCampo(
                        "NRC",
                        txtNrc,
                        180
                )
        );

        JPanel fila2 = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        18,
                        0
                )
        );

        fila2.setOpaque(false);

        fila2.add(
                crearGrupoCampo(
                        "Giro comercial",
                        txtGiroComercial,
                        360
                )
        );

        fila2.add(
                crearGrupoCampo(
                        "Teléfono",
                        txtTelefono,
                        220
                )
        );

        fila2.add(
                crearGrupoCampo(
                        "Correo",
                        txtCorreo,
                        300
                )
        );

        JPanel fila3 = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        18,
                        0
                )
        );

        fila3.setOpaque(false);

        fila3.add(
                crearGrupoCampo(
                        "Dirección",
                        txtDireccion,
                        700
                )
        );

        JPanel acciones = new JPanel(
        new FlowLayout(
                FlowLayout.RIGHT,
                0,
                0
        )
);

acciones.setOpaque(false);

JButton btnGuardarEmpresa =
        crearBotonPrincipal(
                "Guardar empresa"
        );

btnGuardarEmpresa.addActionListener(
        e -> guardarEmpresa()
);

acciones.add(btnGuardarEmpresa);

tarjeta.add(fila1);
tarjeta.add(Box.createVerticalStrut(15));

tarjeta.add(fila2);
tarjeta.add(Box.createVerticalStrut(15));

tarjeta.add(fila3);
tarjeta.add(Box.createVerticalStrut(20));

tarjeta.add(acciones);
return tarjeta;
    }

    private JTextField crearCampoTexto() {

        JTextField campo = new JTextField();

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        200,
                        36
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE),
                        BorderFactory.createEmptyBorder(
                                6,
                                10,
                                6,
                                10
                        )
                )
        );

        return campo;
    }

    private JPanel crearGrupoCampo(
            String etiqueta,
            JTextField campo,
            int ancho
    ) {

        JPanel grupo = new JPanel();

        grupo.setOpaque(false);

        grupo.setLayout(
                new BoxLayout(
                        grupo,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel label = new JLabel(etiqueta);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        label.setForeground(COLOR_TEXTO);

        label.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);

        campo.setPreferredSize(
                new Dimension(
                        ancho,
                        36
                )
        );

        campo.setMaximumSize(
                new Dimension(
                        ancho,
                        36
                )
        );

        grupo.add(label);
        grupo.add(Box.createVerticalStrut(6));
        grupo.add(campo);

        return grupo;
    }

    // =========================================================
    // FORMATOS AUTOMÁTICOS
    // =========================================================

    private void configurarCampoNit() {

        PlainDocument documento =
                (PlainDocument) txtNit.getDocument();

        documento.setDocumentFilter(
                new FiltroFormatoNumerico(
                        14,
                        TipoFormato.NIT
                )
        );
    }

    private void configurarCampoNrc() {

        PlainDocument documento =
                (PlainDocument) txtNrc.getDocument();

        documento.setDocumentFilter(
                new FiltroFormatoNumerico(
                        8,
                        TipoFormato.NRC
                )
        );
    }

    private void configurarCampoTelefono() {

        PlainDocument documento =
                (PlainDocument) txtTelefono.getDocument();

        documento.setDocumentFilter(
                new FiltroFormatoNumerico(
                        8,
                        TipoFormato.TELEFONO
                )
        );
    }

    private enum TipoFormato {
        NIT,
        NRC,
        TELEFONO
    }

    private class FiltroFormatoNumerico extends DocumentFilter {

        private final int maximoDigitos;
        private final TipoFormato tipo;

        public FiltroFormatoNumerico(
                int maximoDigitos,
                TipoFormato tipo
        ) {

            this.maximoDigitos =
                    maximoDigitos;

            this.tipo =
                    tipo;
        }

        @Override
        public void insertString(
                FilterBypass fb,
                int offset,
                String string,
                AttributeSet attr
        ) throws BadLocationException {

            if (string == null) {
                return;
            }

            reemplazarContenido(
                    fb,
                    offset,
                    0,
                    string,
                    attr
            );
        }

        @Override
        public void replace(
                FilterBypass fb,
                int offset,
                int length,
                String text,
                AttributeSet attrs
        ) throws BadLocationException {

            reemplazarContenido(
                    fb,
                    offset,
                    length,
                    text,
                    attrs
            );
        }

        private void reemplazarContenido(
                FilterBypass fb,
                int offset,
                int length,
                String textoNuevo,
                AttributeSet attrs
        ) throws BadLocationException {

            String actual =
                    fb.getDocument()
                            .getText(
                                    0,
                                    fb.getDocument().getLength()
                            );

            StringBuilder combinado =
                    new StringBuilder(actual);

            combinado.replace(
                    offset,
                    offset + length,
                    textoNuevo == null
                            ? ""
                            : textoNuevo
            );

            String numeros =
                    combinado.toString()
                            .replaceAll(
                                    "\\D",
                                    ""
                            );

            if (
                    numeros.length()
                    > maximoDigitos
            ) {

                numeros =
                        numeros.substring(
                                0,
                                maximoDigitos
                        );
            }

            String formateado =
                    switch (tipo) {

                        case NIT ->
                            formatearNit(numeros);

                        case NRC ->
                            formatearNrc(numeros);

                        case TELEFONO ->
                            formatearTelefono(numeros);
                    };

            fb.replace(
                    0,
                    fb.getDocument().getLength(),
                    formateado,
                    attrs
            );
        }
    }

    private String formatearNit(
            String numeros
    ) {

        if (numeros == null || numeros.isEmpty()) {
            return "";
        }

        StringBuilder resultado =
                new StringBuilder();

        for (
                int i = 0;
                i < numeros.length();
                i++
        ) {

            if (
                    i == 4
                    || i == 10
                    || i == 13
            ) {

                resultado.append("-");
            }

            resultado.append(
                    numeros.charAt(i)
            );
        }

        return resultado.toString();
    }

    private String formatearTelefono(
            String numeros
    ) {

        if (numeros == null || numeros.isEmpty()) {
            return "";
        }

        if (numeros.length() <= 4) {
            return numeros;
        }

        return numeros.substring(0, 4)
                + "-"
                + numeros.substring(4);
    }

    private String formatearNrc(
            String numeros
    ) {

        if (numeros == null || numeros.isEmpty()) {
            return "";
        }

        /*
         * Mientras el usuario escribe no colocamos el guion
         * hasta que exista al menos un dígito adicional.
         *
         * Ejemplo:
         * 1234567 -> 123456-7
         *
         * Esto permite trabajar con NRC de distinta longitud
         * sin obligar al usuario a escribir el guion.
         */

        if (numeros.length() <= 2) {
            return numeros;
        }

        return numeros.substring(
                0,
                numeros.length() - 1
        )
                + "-"
                + numeros.substring(
                        numeros.length() - 1
                );
    }

    // =========================================================
    // CARGAR EMPRESA
    // =========================================================

    private void cargarEmpresa() {

        try {

            ConfiguracionContable configuracion =
                    configuracionService.obtener();

            Empresa empresa =
                    empresaService.obtener(
                            configuracion.getIdEmpresa()
                    );

            txtNombreEmpresa.setText(
                    valorSeguro(
                            empresa.getNombre()
                    )
            );

            txtNit.setText(
                    formatearNit(
                            soloNumeros(
                                    empresa.getNit()
                            )
                    )
            );

            txtNrc.setText(
                    formatearNrc(
                            soloNumeros(
                                    empresa.getNrc()
                            )
                    )
            );

            txtGiroComercial.setText(
                    valorSeguro(
                            empresa.getGiroComercial()
                    )
            );

            txtDireccion.setText(
                    valorSeguro(
                            empresa.getDireccion()
                    )
            );

            txtTelefono.setText(
                    formatearTelefono(
                            soloNumeros(
                                    empresa.getTelefono()
                            )
                    )
            );

            txtCorreo.setText(
                    valorSeguro(
                            empresa.getCorreo()
                    )
            );

        } catch (Exception e) {

            mostrarError(
                    e,
                    "No se pudieron cargar los datos de la empresa"
            );
        }
    }

    // =========================================================
    // GUARDAR EMPRESA
    // =========================================================

    private void guardarEmpresa() {

        try {

            if (!validarDatosEmpresa()) {
                return;
            }

            ConfiguracionContable configuracion =
                    configuracionService.obtener();

            Empresa empresa =
                    empresaService.obtener(
                            configuracion.getIdEmpresa()
                    );

            empresa.setNombre(
                    txtNombreEmpresa
                            .getText()
                            .trim()
            );

            empresa.setNit(
                    textoONull(
                            txtNit.getText()
                    )
            );

            empresa.setNrc(
                    textoONull(
                            txtNrc.getText()
                    )
            );

            empresa.setGiroComercial(
                    txtGiroComercial
                            .getText()
                            .trim()
            );

            empresa.setDireccion(
                    txtDireccion
                            .getText()
                            .trim()
            );

            empresa.setTelefono(
                    textoONull(
                            txtTelefono.getText()
                    )
            );

            empresa.setCorreo(
                    textoONull(
                            txtCorreo.getText()
                    )
            );

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Deseas guardar los datos de la empresa?",
                            "Confirmar cambios",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (
                    respuesta
                    != JOptionPane.YES_OPTION
            ) {

                return;
            }

            empresaService.actualizar(
                    empresa
            );

            cargarEmpresa();

            JOptionPane.showMessageDialog(
                    this,
                    "Datos de la empresa actualizados correctamente.",
                    "Empresa actualizada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    e,
                    "No se pudieron guardar los datos de la empresa"
            );
        }
    }

    // =========================================================
    // VALIDACIONES DE EMPRESA
    // =========================================================

    private boolean validarDatosEmpresa() {

        String nombre =
                txtNombreEmpresa
                        .getText()
                        .trim();

        String nit =
                soloNumeros(
                        txtNit.getText()
                );

        String nrc =
                soloNumeros(
                        txtNrc.getText()
                );

        String giro =
                txtGiroComercial
                        .getText()
                        .trim();

        String direccion =
                txtDireccion
                        .getText()
                        .trim();

        String telefono =
                soloNumeros(
                        txtTelefono.getText()
                );

        String correo =
                txtCorreo
                        .getText()
                        .trim();

        if (nombre.isBlank()) {

            mostrarAdvertencia(
                    "El nombre o razón social de la empresa es obligatorio."
            );

            txtNombreEmpresa.requestFocus();

            return false;
        }

        if (nombre.length() < 3) {

            mostrarAdvertencia(
                    "El nombre o razón social debe contener al menos 3 caracteres."
            );

            txtNombreEmpresa.requestFocus();

            return false;
        }

        if (
                !nit.isEmpty()
                && nit.length() != 14
        ) {

            mostrarAdvertencia(
                    "El NIT debe contener 14 dígitos.\n\n"
                    + "No es necesario escribir los guiones; "
                    + "el sistema los coloca automáticamente."
            );

            txtNit.requestFocus();

            return false;
        }

        if (
                !nrc.isEmpty()
                && nrc.length() < 2
        ) {

            mostrarAdvertencia(
                    "El NRC ingresado no es válido."
            );

            txtNrc.requestFocus();

            return false;
        }

        if (giro.isBlank()) {

            mostrarAdvertencia(
                    "El giro comercial de la empresa es obligatorio."
            );

            txtGiroComercial.requestFocus();

            return false;
        }

        if (direccion.isBlank()) {

            mostrarAdvertencia(
                    "La dirección de la empresa es obligatoria."
            );

            txtDireccion.requestFocus();

            return false;
        }

        if (
                !telefono.isEmpty()
                && telefono.length() != 8
        ) {

            mostrarAdvertencia(
                    "El teléfono debe contener 8 dígitos.\n\n"
                    + "Ejemplo: 2222-3333"
            );

            txtTelefono.requestFocus();

            return false;
        }

        if (
                !correo.isEmpty()
                && !PATRON_CORREO
                        .matcher(correo)
                        .matches()
        ) {

            mostrarAdvertencia(
                    "El correo electrónico no tiene un formato válido.\n\n"
                    + "Ejemplo: empresa@correo.com"
            );

            txtCorreo.requestFocus();

            return false;
        }

        return true;
    }

    private void mostrarAdvertencia(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Verifica la información",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private String soloNumeros(
            String valor
    ) {

        if (valor == null) {
            return "";
        }

        return valor.replaceAll(
                "\\D",
                ""
        );
    }

    private String textoONull(
            String valor
    ) {

        if (valor == null) {
            return null;
        }

        String limpio =
                valor.trim();

        return limpio.isEmpty()
                ? null
                : limpio;
    }

    private String valorSeguro(
            String valor
    ) {

        return valor == null
                ? ""
                : valor;
    }

    // =========================================================
    // CONFIGURACIÓN DE IVA
    // =========================================================

    private JPanel crearTarjetaIva() {

        JPanel tarjeta =
                new JPanel();

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                22,
                                22,
                                22,
                                22
                        )
                )
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        310
                )
        );

        tarjeta.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JLabel titulo =
                new JLabel(
                        "Configuración de IVA"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );

        lblConfiguracionActual =
                new JLabel(
                        "Cargando configuración..."
                );

        lblConfiguracionActual.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblConfiguracionActual.setForeground(
                COLOR_SECUNDARIO
        );

        JPanel campos =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                0
                        )
                );

        campos.setOpaque(false);

        JLabel lblPorcentaje =
                new JLabel(
                        "IVA (%):"
                );

        lblPorcentaje.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblPorcentaje.setForeground(
                COLOR_TEXTO
        );

        txtPorcentajeIva =
                new JTextField(8);

        txtPorcentajeIva.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        txtPorcentajeIva.setPreferredSize(
                new Dimension(
                        100,
                        36
                )
        );

        JLabel lblModo =
                new JLabel(
                        "Modo:"
                );

        lblModo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblModo.setForeground(
                COLOR_TEXTO
        );

        cmbTipoIva =
                new JComboBox<>(
                        new String[]{
                            "IVA incluido en el monto",
                            "Monto + IVA"
                        }
                );

        cmbTipoIva.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        cmbTipoIva.setPreferredSize(
                new Dimension(
                        210,
                        36
                )
        );

        JButton btnGuardar =
                crearBotonPrincipal(
                        "Guardar IVA"
                );

        btnGuardar.addActionListener(
                e -> guardarIva()
        );

        campos.add(lblPorcentaje);
        campos.add(txtPorcentajeIva);
        campos.add(lblModo);
        campos.add(cmbTipoIva);
        campos.add(btnGuardar);

        JLabel ayuda =
                new JLabel(
                        "<html><div style='width:760px;'>"
                        + "<b>IVA incluido:</b> si escribes 10,000, ese valor ya contiene IVA. "
                        + "<b>Monto + IVA:</b> si escribes 10,000, el sistema agrega el IVA encima."
                        + "<br><br><b>Importante:</b> el cambio aplica solamente a operaciones nuevas; "
                        + "los asientos ya contabilizados no se modifican."
                        + "</div></html>"
                );

        ayuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        ayuda.setForeground(
                COLOR_SECUNDARIO
        );

        tarjeta.add(titulo);
        tarjeta.add(
                Box.createVerticalStrut(10)
        );

        tarjeta.add(
                lblConfiguracionActual
        );

        tarjeta.add(
                Box.createVerticalStrut(18)
        );

        tarjeta.add(campos);

        tarjeta.add(
                Box.createVerticalStrut(16)
        );

        tarjeta.add(ayuda);

        return tarjeta;
    }

    public final void cargarConfiguracion() {

        try {

            ConfiguracionContable c =
                    configuracionService.obtener();

            txtPorcentajeIva.setText(
                    c.getPorcentajeIva()
                            .stripTrailingZeros()
                            .toPlainString()
            );

            cmbTipoIva.setSelectedIndex(
                    "MAS_IVA".equals(
                            c.getTipoIva()
                    )
                            ? 1
                            : 0
            );

            lblConfiguracionActual.setText(
                    "Configuración actual: IVA "
                    + c.getPorcentajeIva()
                            .stripTrailingZeros()
                            .toPlainString()
                    + "% · "
                    + (
                            "MAS_IVA".equals(
                                    c.getTipoIva()
                            )
                                    ? "Monto + IVA"
                                    : "IVA incluido"
                    )
                    + " · Moneda "
                    + c.getMoneda()
            );

        } catch (Exception e) {

            mostrarError(
                    e,
                    "No se pudo cargar la configuración"
            );
        }
    }

    private void guardarIva() {

        try {

            String texto =
                    txtPorcentajeIva
                            .getText()
                            .trim()
                            .replace(
                                    ",",
                                    "."
                            );

            BigDecimal porcentaje =
                    new BigDecimal(texto);

            String tipo =
                    cmbTipoIva
                            .getSelectedIndex() == 1
                            ? "MAS_IVA"
                            : "INCLUIDO";

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Guardar esta configuración de IVA?\n\n"
                            + "IVA: "
                            + porcentaje
                            + "%\n"
                            + "Modo: "
                            + (
                                    "MAS_IVA".equals(tipo)
                                            ? "Monto + IVA"
                                            : "IVA incluido"
                            )
                            + "\n\nSolo afectará operaciones nuevas.",
                            "Confirmar configuración",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (
                    respuesta
                    != JOptionPane.YES_OPTION
            ) {

                return;
            }

            configuracionService.actualizarIva(
                    porcentaje,
                    tipo
            );

            cargarConfiguracion();

            JOptionPane.showMessageDialog(
                    this,
                    "Configuración de IVA actualizada correctamente.",
                    "IVA actualizado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El porcentaje de IVA no es válido.",
                    "Dato incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    e,
                    "No se pudo guardar la configuración"
            );
        }
    }

    // =========================================================
    // ZONA DE PRUEBAS
    // =========================================================

    private JPanel crearTarjetaReinicio() {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        254,
                                        202,
                                        202
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                22,
                                22,
                                22,
                                22
                        )
                )
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        190
                )
        );

        tarjeta.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Zona de pruebas"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );

        JLabel descripcion =
                new JLabel(
                        "<html><div style='width:650px;'>"
                        + "Reinicia operaciones, asientos, detalles, Kardex, capas PEPS y saldos iniciales."
                        + "<br>Conserva empresa, configuración, catálogo, usuarios y productos; deja un único período abierto para volver a probar."
                        + "</div></html>"
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        descripcion.setForeground(
                COLOR_SECUNDARIO
        );

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(8)
        );

        textos.add(descripcion);

        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        acciones.setOpaque(false);

        JButton btnReiniciar =
                crearBotonPeligro(
                        "Reiniciar datos de prueba"
                );

        btnReiniciar.addActionListener(
                e -> confirmarReinicio()
        );

        acciones.add(
                btnReiniciar
        );

        tarjeta.add(
                textos,
                BorderLayout.CENTER
        );

        tarjeta.add(
                acciones,
                BorderLayout.SOUTH
        );

        return tarjeta;
    }

    // =========================================================
    // BOTONES
    // =========================================================

    private JButton crearBotonPrincipal(
            String texto
    ) {

        JButton boton =
                new JButton(texto);

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                COLOR_PRIMARIO
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        16,
                        10,
                        16
                )
        );

        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return boton;
    }

    private JButton crearBotonPeligro(
            String texto
    ) {

        JButton boton =
                new JButton(texto);

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                COLOR_ROJO
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        11,
                        18,
                        11,
                        18
                )
        );

        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                COLOR_ROJO_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                COLOR_ROJO
                        );
                    }
                }
        );

        return boton;
    }

    // =========================================================
    // REINICIO
    // =========================================================

    private void confirmarReinicio() {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Estás seguro de que deseas limpiar los datos de prueba?\n\n"
                        + "Se eliminarán operaciones, asientos, detalles, Kardex, capas PEPS, históricos de períodos de prueba y saldos iniciales.\n"
                        + "Se conservarán empresa, usuarios, catálogo, configuración y productos.",
                        "Confirmar reinicio",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                respuesta
                != JOptionPane.YES_OPTION
        ) {

            return;
        }

        int segunda =
                JOptionPane.showConfirmDialog(
                        this,
                        "Esta acción no se puede deshacer.\n\n¿Deseas continuar?",
                        "Última confirmación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                segunda
                != JOptionPane.YES_OPTION
        ) {

            return;
        }

        try {

            String resultado =
                    reinicioService
                            .reiniciarDatosPrueba();

            JOptionPane.showMessageDialog(
                    this,
                    resultado,
                    "Sistema reiniciado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    e,
                    "Error al reiniciar"
            );
        }
    }

    // =========================================================
    // ERRORES
    // =========================================================

    private void mostrarError(
            Throwable error,
            String titulo
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Ocurrió un error desconocido.";

        while (actual != null) {

            if (
                    actual.getMessage() != null
                    && !actual.getMessage().isBlank()
            ) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                JOptionPane.ERROR_MESSAGE
        );
    }
}