package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ClientesPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtCorreo;
    private JButton btnAgregar, btnEliminar, btnRefrescar;

    public ClientesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));

        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelTabla(),     BorderLayout.CENTER);
        add(crearPanelBotones(),   BorderLayout.SOUTH);

        cargarClientes();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNombre = new JTextField(20);
        panel.add(txtNombre, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtCorreo = new JTextField(20);
        panel.add(txtCorreo, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        btnAgregar = new JButton("+ Agregar");
        btnAgregar.setBackground(new Color(46, 204, 113));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.addActionListener(e -> agregarCliente());
        panel.add(btnAgregar, gbc);

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID", "Nombre", "Correo"};
        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(34, 45, 65));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setShowVerticalLines(false);

        // Ancho columna ID
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return scroll;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(245, 247, 250));

        btnEliminar = new JButton("🗑 Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarCliente());
        panel.add(btnEliminar);

        btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarClientes());
        panel.add(btnRefrescar);

        return panel;
    }

    private void cargarClientes() {
        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.LISTAR_CLIENTES, null);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                if (resp.isExito()) {
                    @SuppressWarnings("unchecked")
                    List<Cliente> lista = (List<Cliente>) resp.getRespuesta();
                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for (Cliente c : lista) {
                            modelo.addRow(new Object[]{c.getIdCliente(), c.getNombre(), c.getCorreo()});
                        }
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void agregarCliente() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!correo.contains("@")) {
            JOptionPane.showMessageDialog(this, "Correo inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnAgregar.setEnabled(false);
        new Thread(() -> {
            try {
                Cliente c = new Cliente(0, nombre, correo);
                Solicitud req = new Solicitud(Solicitud.AGREGAR_CLIENTE, c);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                SwingUtilities.invokeLater(() -> {
                    if (resp.isExito()) {
                        txtNombre.setText("");
                        txtCorreo.setText("");
                        cargarClientes();
                        JOptionPane.showMessageDialog(this, resp.getMensaje());
                    } else {
                        JOptionPane.showMessageDialog(this, resp.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    btnAgregar.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnAgregar.setEnabled(true);
                });
            }
        }).start();
    }

    private void eliminarCliente() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        String nombre = (String) modelo.getValueAt(fila, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar cliente '" + nombre + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.ELIMINAR_CLIENTE, id);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, resp.getMensaje());
                    if (resp.isExito()) cargarClientes();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}
