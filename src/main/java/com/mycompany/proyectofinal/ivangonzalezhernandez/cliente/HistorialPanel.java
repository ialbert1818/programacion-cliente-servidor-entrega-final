package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;

/**
 * Panel que muestra el historial de facturas y permite ver detalle completo.
 */
public class HistorialPanel extends JPanel {

    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;
    private JTextArea txtDetalle;
    private JButton btnRefrescar, btnVerDetalle, btnExportarTxt;
    private List<Factura> listaFacturas;

    public HistorialPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));

        add(crearPanelTabla(),   BorderLayout.CENTER);
        add(crearPanelDetalle(), BorderLayout.EAST);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        cargarHistorial();
    }

    private JScrollPane crearPanelTabla() {
        String[] cols = {"# Factura", "Cliente", "Fecha", "Total"};
        modeloFacturas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFacturas.setRowHeight(28);
        tablaFacturas.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaFacturas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaFacturas.getTableHeader().setBackground(new Color(34, 45, 65));
        tablaFacturas.getTableHeader().setForeground(Color.WHITE);
        tablaFacturas.setGridColor(new Color(220, 220, 220));

        // Colores alternados
        tablaFacturas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 245, 255));
                return this;
            }
        });

        // Doble click -> ver detalle
        tablaFacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verDetalle();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaFacturas);
        scroll.setBorder(BorderFactory.createTitledBorder("Facturas Registradas"));
        scroll.setPreferredSize(new Dimension(500, 400));
        return scroll;
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Detalle de Factura"));
        panel.setPreferredSize(new Dimension(320, 400));

        txtDetalle = new JTextArea();
        txtDetalle.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDetalle.setEditable(false);
        txtDetalle.setBackground(new Color(252, 252, 252));
        txtDetalle.setText("Seleccione una factura\nde la lista para ver\nel detalle completo.\n\n" +
                           "Doble clic o botón\n'Ver Detalle'.");

        panel.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);

        btnExportarTxt = new JButton("💾 Exportar .txt");
        btnExportarTxt.setFocusPainted(false);
        btnExportarTxt.addActionListener(e -> exportarTxt());
        panel.add(btnExportarTxt, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(245, 247, 250));

        btnRefrescar = new JButton("🔄 Refrescar Historial");
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarHistorial());
        panel.add(btnRefrescar);

        btnVerDetalle = new JButton("🔍 Ver Detalle");
        btnVerDetalle.setBackground(new Color(52, 152, 219));
        btnVerDetalle.setForeground(Color.WHITE);
        btnVerDetalle.setFocusPainted(false);
        btnVerDetalle.addActionListener(e -> verDetalle());
        panel.add(btnVerDetalle);

        JLabel lblAyuda = new JLabel("  (Doble clic en una fila para ver el detalle)");
        lblAyuda.setForeground(Color.GRAY);
        lblAyuda.setFont(new Font("Arial", Font.ITALIC, 11));
        panel.add(lblAyuda);

        return panel;
    }

    private void cargarHistorial() {
        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.LISTAR_FACTURAS, null);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                if (resp.isExito()) {
                    @SuppressWarnings("unchecked")
                    List<Factura> lista = (List<Factura>) resp.getRespuesta();
                    listaFacturas = lista;
                    SwingUtilities.invokeLater(() -> {
                        modeloFacturas.setRowCount(0);
                        for (Factura f : lista) {
                            modeloFacturas.addRow(new Object[]{
                                f.getNumeroFactura(),
                                f.getCliente() != null ? f.getCliente().getNombre() : "?",
                                f.getFecha(),
                                String.format("$%.2f", f.getTotal())
                            });
                        }
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error cargando historial: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void verDetalle() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idFactura = (int) modeloFacturas.getValueAt(fila, 0);

        txtDetalle.setText("Cargando...");
        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.OBTENER_FACTURA, idFactura);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                SwingUtilities.invokeLater(() -> {
                    if (resp.isExito()) {
                        Factura f = (Factura) resp.getRespuesta();
                        txtDetalle.setText(f.generarTextoFactura());
                        txtDetalle.setCaretPosition(0);
                    } else {
                        txtDetalle.setText("Error: " + resp.getMensaje());
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    txtDetalle.setText("Error al obtener detalle:\n" + e.getMessage()));
            }
        }).start();
    }

    private void exportarTxt() {
        String contenido = txtDetalle.getText();
        if (contenido.startsWith("Seleccione") || contenido.startsWith("Cargando")) {
            JOptionPane.showMessageDialog(this, "Primero seleccione y cargue una factura.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fila = tablaFacturas.getSelectedRow();
        int id = fila >= 0 ? (int) modeloFacturas.getValueAt(fila, 0) : 0;
        String nombreArchivo = "Factura_" + id + ".txt";

        try (FileWriter fw = new FileWriter(nombreArchivo)) {
            fw.write(contenido);
            JOptionPane.showMessageDialog(this, "Factura exportada: " + nombreArchivo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
