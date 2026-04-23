package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.Usuario;
import java.sql.*;

public class UsuarioDAO {

    private Connection conn;

    public UsuarioDAO() throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public Usuario login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM USUARIOS WHERE USERNAME=? AND PASSWORD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
        }
        return null;
    }

    public boolean agregarUsuario(Usuario u) throws SQLException {
        String sql = "INSERT INTO USUARIOS (USERNAME, PASSWORD, ROL) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRol());
            return ps.executeUpdate() > 0;
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("ID_USUARIO"));
        u.setUsername(rs.getString("USERNAME"));
        u.setPassword(rs.getString("PASSWORD"));
        u.setRol(rs.getString("ROL"));
        return u;
    }
}
