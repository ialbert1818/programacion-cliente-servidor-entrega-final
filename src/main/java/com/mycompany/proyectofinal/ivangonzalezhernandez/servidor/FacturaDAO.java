package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    private Connection conn;
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;

    public FacturaDAO() throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
    }

    public Factura guardarFactura(Factura factura) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Insertar cabecera factura
            String sqlFactura = "INSERT INTO FACTURAS (ID_CLIENTE, FECHA, TOTAL) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, factura.getCliente().getIdCliente());
                ps.setDate(2, Date.valueOf(factura.getFecha()));
                ps.setDouble(3, factura.getTotal());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) factura.setNumeroFactura(keys.getInt(1));
            }

            // Insertar detalles y actualizar stock
            String sqlDetalle = "INSERT INTO DETALLE_FACTURA (ID_FACTURA, ID_PRODUCTO, CANTIDAD, SUBTOTAL) VALUES (?,?,?,?)";
            for (DetalleFactura d : factura.getDetalles()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
                    ps.setInt(1, factura.getNumeroFactura());
                    ps.setInt(2, d.getProducto().getIdProducto());
                    ps.setInt(3, d.getCantidad());
                    ps.setDouble(4, d.getSubtotal());
                    ps.executeUpdate();
                }
                // Reducir stock
                Producto p = d.getProducto();
                productoDAO.actualizarCantidad(p.getIdProducto(), p.getCantidad() - d.getCantidad());
            }

            conn.commit();
            return factura;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Factura> listarFacturas() throws SQLException {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM FACTURAS ORDER BY ID_FACTURA DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Factura f = new Factura();
                f.setNumeroFactura(rs.getInt("ID_FACTURA"));
                f.setFecha(rs.getDate("FECHA").toLocalDate());
                f.setTotal(rs.getDouble("TOTAL"));
                Cliente c = clienteDAO.buscarPorId(rs.getInt("ID_CLIENTE"));
                f.setCliente(c);
                lista.add(f);
            }
        }
        return lista;
    }

    public Factura obtenerFacturaCompleta(int idFactura) throws SQLException {
        String sqlF = "SELECT * FROM FACTURAS WHERE ID_FACTURA=?";
        Factura factura = null;
        try (PreparedStatement ps = conn.prepareStatement(sqlF)) {
            ps.setInt(1, idFactura);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                factura = new Factura();
                factura.setNumeroFactura(idFactura);
                factura.setFecha(rs.getDate("FECHA").toLocalDate());
                factura.setTotal(rs.getDouble("TOTAL"));
                factura.setCliente(clienteDAO.buscarPorId(rs.getInt("ID_CLIENTE")));
            }
        }
        if (factura == null) return null;

        String sqlD = "SELECT * FROM DETALLE_FACTURA WHERE ID_FACTURA=?";
        try (PreparedStatement ps = conn.prepareStatement(sqlD)) {
            ps.setInt(1, idFactura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = productoDAO.buscarPorId(rs.getInt("ID_PRODUCTO"));
                DetalleFactura d = new DetalleFactura();
                d.setProducto(p);
                d.setCantidad(rs.getInt("CANTIDAD"));
                d.setSubtotal(rs.getDouble("SUBTOTAL"));
                factura.getDetalles().add(d);
            }
        }
        return factura;
    }
}
