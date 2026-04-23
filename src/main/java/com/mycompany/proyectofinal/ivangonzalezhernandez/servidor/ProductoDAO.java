package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Connection conn;

    public ProductoDAO() throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Producto> listarProductos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS ORDER BY ID_PRODUCTO";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapProducto(rs));
            }
        }
        return lista;
    }

    public Producto agregar(Producto p) throws SQLException {
        String sql = "INSERT INTO PRODUCTOS (NOMBRE, PRECIO, CANTIDAD, TIPO) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getCantidad());
            ps.setString(4, p.getTipo());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) p.setIdProducto(keys.getInt(1));
        }
        return p;
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM PRODUCTOS WHERE ID_PRODUCTO=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarCantidad(int idProducto, int nuevaCantidad) throws SQLException {
        String sql = "UPDATE PRODUCTOS SET CANTIDAD=? WHERE ID_PRODUCTO=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }

    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM PRODUCTOS WHERE ID_PRODUCTO=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProducto(rs);
        }
        return null;
    }

    private Producto mapProducto(ResultSet rs) throws SQLException {
        String tipo = rs.getString("TIPO");
        int id = rs.getInt("ID_PRODUCTO");
        String nombre = rs.getString("NOMBRE");
        double precio = rs.getDouble("PRECIO");
        int cantidad = rs.getInt("CANTIDAD");

        if ("ALIMENTO".equals(tipo)) {
            return new ProductoAlimento(id, nombre, precio, cantidad);
        } else {
            return new ProductoElectronico(id, nombre, precio, cantidad);
        }
    }
}
