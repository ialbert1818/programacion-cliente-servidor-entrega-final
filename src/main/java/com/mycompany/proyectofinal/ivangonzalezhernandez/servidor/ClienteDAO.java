package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private Connection conn;

    public ClienteDAO() throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTES ORDER BY ID_CLIENTE";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
        }
        return lista;
    }

    public Cliente agregar(Cliente c) throws SQLException {
        String sql = "INSERT INTO CLIENTES (NOMBRE, CORREO) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getCorreo());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) c.setIdCliente(keys.getInt(1));
        }
        return c;
    }

    public boolean eliminar(int id) throws SQLException {
        // Verificar que no tenga facturas
        String check = "SELECT COUNT(*) FROM FACTURAS WHERE ID_CLIENTE=?";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false;
        }
        String sql = "DELETE FROM CLIENTES WHERE ID_CLIENTE=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM CLIENTES WHERE ID_CLIENTE=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCliente(rs);
        }
        return null;
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("ID_CLIENTE"));
        c.setNombre(rs.getString("NOMBRE"));
        c.setCorreo(rs.getString("CORREO"));
        return c;
    }
}
