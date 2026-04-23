package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ProductosPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtPrecio, txtCantidad;
    private JComboBox<String> cmbTipo;
    private JButton btnAgregar, btnEliminar, btnRefrescar;

    public ProductosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));

        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelTabla(),     BorderLayout.CENTER);
        add(crearPanelBotones(),   BorderLayout.SOUTH);

        cargarProductos();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; txtNombre = new JTextField(15); panel.add(txtNombre, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panel.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; txtPrecio = new JTextField(8); panel.add(txtPrecio, gbc);

        // Fila 2
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0; panel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; txtCantidad = new JTextField(8); panel.add(txtCantidad, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        cmbTipo = new JComboBox<>(new String[]{"ALIMENTO (IVA 5%)", "ELECTRONICO (IVA 13%)"});
        panel.add(cmbTipo, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0;
        btnAgregar = new JButton("+ Agregar");
        btnAgregar.setBackground(new Color(46, 204, 113));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.addActionListener(e -> agregarProducto());
        panel.add(btnAgregar, gbc);

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID", "Nombre", "Precio Base", "IVA", "Precio Final", "Stock", "Tipo"};
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

        // Ajuste de anchos
        int[] anchos = {45, 180, 90, 60, 100, 60, 100};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        // Coloreado de filas por tipo
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String tipo = (String) modelo.getValueAt(row, 6);
                    setBackground("ALIMENTO".equals(tipo)
                        ? new Color(232, 255, 232)
                        : new Color(230, 240, 255));
                }
                return this;
            }
        });

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
        btnEliminar.addActionListener(e -> eliminarProducto());
        panel.add(btnEliminar);

        btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarProductos());
        panel.add(btnRefrescar);

        return panel;
    }

    private void cargarProductos() {
        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.LISTAR_PRODUCTOS, null);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                if (resp.isExito()) {
                    @SuppressWarnings("unchecked")
                    List<Producto> lista = (List<Producto>) resp.getRespuesta();
                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for (Producto p : lista) {
                            double iva = p.calcularPrecioFinal() - p.getPrecio();
                            modelo.addRow(new Object[]{
                                p.getIdProducto(),
                                p.getNombre(),
                                String.format("$%.2f", p.getPrecio()),
                                String.format("$%.2f", iva),
                                String.format("$%.2f", p.calcularPrecioFinal()),
                                p.getCantidad(),
                                p.getTipo()
                            });
                        }
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void agregarProducto() {
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String cantStr = txtCantidad.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || cantStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio;
        int cantidad;
        try {
            precio = Double.parseDouble(precioStr);
            cantidad = Integer.parseInt(cantStr);
            if (precio <= 0 || cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y cantidad deben ser números positivos.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean esAlimento = cmbTipo.getSelectedIndex() == 0;
        Producto p = esAlimento
            ? new ProductoAlimento(0, nombre, precio, cantidad)
            : new ProductoElectronico(0, nombre, precio, cantidad);

        btnAgregar.setEnabled(false);
        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.AGREGAR_PRODUCTO, p);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                SwingUtilities.invokeLater(() -> {
                    if (resp.isExito()) {
                        txtNombre.setText(""); txtPrecio.setText(""); txtCantidad.setText("");
                        cargarProductos();
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

    private void eliminarProducto() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        String nombre = (String) modelo.getValueAt(fila, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar producto '" + nombre + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.ELIMINAR_PRODUCTO, id);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, resp.getMensaje());
                    if (resp.isExito()) cargarProductos();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    /** Permite a FacturaPanel obtener la lista de productos en memoria */
    public void recargar() { cargarProductos(); }
}
