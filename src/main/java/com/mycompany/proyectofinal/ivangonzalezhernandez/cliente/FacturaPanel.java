package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para generar una nueva factura.
 * Permite seleccionar cliente, agregar productos con cantidad y ver el resumen.
 */
public class FacturaPanel extends JPanel {

    // Datos cargados del servidor
    private List<Cliente>  listaClientes  = new ArrayList<>();
    private List<Producto> listaProductos = new ArrayList<>();

    // Carrito de compras
    private List<DetalleFactura> carrito = new ArrayList<>();
    private double totalActual = 0.0;

    // Componentes UI
    private JComboBox<String> cmbCliente;
    private JComboBox<String> cmbProducto;
    private JSpinner spinCantidad;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotal;
    private JTextArea txtVistaFactura;
    private JButton btnAgregarItem, btnQuitarItem, btnGenerarFactura, btnLimpiar;

    public FacturaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));

        add(crearPanelSeleccion(),  BorderLayout.NORTH);
        add(crearPanelCentro(),     BorderLayout.CENTER);
        add(crearPanelAcciones(),   BorderLayout.SOUTH);

        cargarDatos();
    }

    // ─── Panel superior: selección de cliente y producto ──────────────────────
    private JPanel crearPanelSeleccion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Datos de Factura"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: cliente
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; panel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbCliente = new JComboBox<>();
        cmbCliente.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(cmbCliente, gbc);

        JButton btnRefClientes = new JButton("↺");
        btnRefClientes.setToolTipText("Recargar clientes");
        btnRefClientes.addActionListener(e -> cargarDatos());
        gbc.gridx = 2; gbc.weightx = 0; panel.add(btnRefClientes, gbc);

        // Fila 1: producto + cantidad + botón agregar
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0; panel.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbProducto = new JComboBox<>();
        cmbProducto.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(cmbProducto, gbc);

        gbc.gridx = 2; gbc.weightx = 0; panel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0;
        spinCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spinCantidad.setPreferredSize(new Dimension(70, 26));
        panel.add(spinCantidad, gbc);

        gbc.gridx = 4;
        btnAgregarItem = new JButton("+ Agregar al carrito");
        btnAgregarItem.setBackground(new Color(52, 152, 219));
        btnAgregarItem.setForeground(Color.WHITE);
        btnAgregarItem.setFocusPainted(false);
        btnAgregarItem.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregarItem.addActionListener(e -> agregarAlCarrito());
        panel.add(btnAgregarItem, gbc);

        return panel;
    }

    // ─── Panel central: carrito izquierda, preview derecha ────────────────────
    private JSplitPane crearPanelCentro() {
        // Tabla carrito
        String[] cols = {"#", "Producto", "Tipo", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloCarrito = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(26);
        tablaCarrito.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaCarrito.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaCarrito.getTableHeader().setBackground(new Color(34, 45, 65));
        tablaCarrito.getTableHeader().setForeground(Color.WHITE);

        int[] anchos = {35, 200, 100, 100, 80, 100};
        for (int i = 0; i < anchos.length; i++)
            tablaCarrito.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JPanel panelCarrito = new JPanel(new BorderLayout(5, 5));
        panelCarrito.setBorder(BorderFactory.createTitledBorder("Carrito de Compra"));
        panelCarrito.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        lblTotal = new JLabel("TOTAL: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(34, 45, 65));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        panelCarrito.add(lblTotal, BorderLayout.SOUTH);

        // Vista previa de factura
        txtVistaFactura = new JTextArea();
        txtVistaFactura.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtVistaFactura.setEditable(false);
        txtVistaFactura.setBackground(new Color(250, 250, 250));
        txtVistaFactura.setText("La vista previa aparecerá aquí\nal agregar productos...");

        JPanel panelPreview = new JPanel(new BorderLayout());
        panelPreview.setBorder(BorderFactory.createTitledBorder("Vista Previa Factura"));
        panelPreview.add(new JScrollPane(txtVistaFactura), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCarrito, panelPreview);
        split.setDividerLocation(500);
        split.setResizeWeight(0.6);
        return split;
    }

    // ─── Panel inferior: botones ───────────────────────────────────────────────
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setBackground(new Color(245, 247, 250));

        btnQuitarItem = new JButton("✖ Quitar ítem");
        btnQuitarItem.setBackground(new Color(230, 126, 34));
        btnQuitarItem.setForeground(Color.WHITE);
        btnQuitarItem.setFocusPainted(false);
        btnQuitarItem.addActionListener(e -> quitarDelCarrito());
        panel.add(btnQuitarItem);

        btnLimpiar = new JButton("🗑 Limpiar todo");
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        panel.add(btnLimpiar);

        btnGenerarFactura = new JButton("✔ GENERAR FACTURA");
        btnGenerarFactura.setBackground(new Color(39, 174, 96));
        btnGenerarFactura.setForeground(Color.WHITE);
        btnGenerarFactura.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerarFactura.setFocusPainted(false);
        btnGenerarFactura.addActionListener(e -> generarFactura());
        panel.add(btnGenerarFactura);

        return panel;
    }

    // ─── Lógica ────────────────────────────────────────────────────────────────
    private void cargarDatos() {
        new Thread(() -> {
            try {
                // Clientes
                Solicitud reqC = new Solicitud(Solicitud.LISTAR_CLIENTES, null);
                Solicitud respC = ConexionServidor.getInstance().enviar(reqC);
                // Productos
                Solicitud reqP = new Solicitud(Solicitud.LISTAR_PRODUCTOS, null);
                Solicitud respP = ConexionServidor.getInstance().enviar(reqP);

                SwingUtilities.invokeLater(() -> {
                    if (respC.isExito()) {
                        @SuppressWarnings("unchecked")
                        List<Cliente> cs = (List<Cliente>) respC.getRespuesta();
                        listaClientes = cs;
                        cmbCliente.removeAllItems();
                        for (Cliente c : listaClientes)
                            cmbCliente.addItem(c.getIdCliente() + " - " + c.getNombre());
                    }
                    if (respP.isExito()) {
                        @SuppressWarnings("unchecked")
                        List<Producto> ps = (List<Producto>) respP.getRespuesta();
                        listaProductos = ps;
                        cmbProducto.removeAllItems();
                        for (Producto p : listaProductos)
                            cmbProducto.addItem(p.getIdProducto() + " - " + p.getNombre()
                                + " [$" + String.format("%.2f", p.calcularPrecioFinal()) + "] Stock:" + p.getCantidad());
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void agregarAlCarrito() {
        int idxProd = cmbProducto.getSelectedIndex();
        if (idxProd < 0 || listaProductos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto prod = listaProductos.get(idxProd);
        int cant = (int) spinCantidad.getValue();

        // Verificar stock considerando lo ya en carrito
        int enCarrito = carrito.stream()
            .filter(d -> d.getProducto().getIdProducto() == prod.getIdProducto())
            .mapToInt(DetalleFactura::getCantidad).sum();

        if (cant + enCarrito > prod.getCantidad()) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente. Disponible: " + (prod.getCantidad() - enCarrito),
                "Sin Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DetalleFactura detalle = new DetalleFactura(prod, cant);
        carrito.add(detalle);
        totalActual += detalle.getSubtotal();

        // Agregar fila a tabla
        modeloCarrito.addRow(new Object[]{
            modeloCarrito.getRowCount() + 1,
            prod.getNombre(),
            prod.getTipo(),
            String.format("$%.2f", prod.calcularPrecioFinal()),
            cant,
            String.format("$%.2f", detalle.getSubtotal())
        });

        lblTotal.setText("TOTAL: $" + String.format("%.2f", totalActual));
        actualizarVistaPrevia();
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del carrito.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        totalActual -= carrito.get(fila).getSubtotal();
        carrito.remove(fila);
        modeloCarrito.removeRow(fila);
        // Renumerar
        for (int i = 0; i < modeloCarrito.getRowCount(); i++)
            modeloCarrito.setValueAt(i + 1, i, 0);

        lblTotal.setText("TOTAL: $" + String.format("%.2f", totalActual));
        actualizarVistaPrevia();
    }

    private void limpiarCarrito() {
        carrito.clear();
        modeloCarrito.setRowCount(0);
        totalActual = 0.0;
        lblTotal.setText("TOTAL: $0.00");
        txtVistaFactura.setText("La vista previa aparecerá aquí\nal agregar productos...");
    }

    private void actualizarVistaPrevia() {
        if (carrito.isEmpty() || cmbCliente.getSelectedIndex() < 0) return;

        Cliente c = listaClientes.get(cmbCliente.getSelectedIndex());
        Factura f = new Factura(c);
        for (DetalleFactura d : carrito) f.agregarDetalle(d);

        txtVistaFactura.setText(f.generarTextoFactura());
    }

    private void generarFactura() {
        if (cmbCliente.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = listaClientes.get(cmbCliente.getSelectedIndex());
        Factura factura = new Factura(cliente);
        for (DetalleFactura d : carrito) factura.agregarDetalle(d);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar factura por $" + String.format("%.2f", factura.getTotal()) + "?",
            "Confirmar Factura", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        btnGenerarFactura.setEnabled(false);
        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.GENERAR_FACTURA, factura);
                Solicitud resp = ConexionServidor.getInstance().enviar(req);
                SwingUtilities.invokeLater(() -> {
                    if (resp.isExito()) {
                        Factura guardada = (Factura) resp.getRespuesta();
                        JOptionPane.showMessageDialog(this,
                            "✔ " + resp.getMensaje() + "\n\n" + guardada.generarTextoFactura(),
                            "Factura Generada", JOptionPane.INFORMATION_MESSAGE);
                        limpiarCarrito();
                        cargarDatos(); // Refrescar stock
                    } else {
                        JOptionPane.showMessageDialog(this, resp.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    btnGenerarFactura.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnGenerarFactura.setEnabled(true);
                });
            }
        }).start();
    }
}
