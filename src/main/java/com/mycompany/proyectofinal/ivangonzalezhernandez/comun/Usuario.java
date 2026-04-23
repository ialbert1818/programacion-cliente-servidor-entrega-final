package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String username;
    private String password;
    private String rol; // "ADMIN" o "VENDEDOR"

    public Usuario() {}

    public Usuario(int id, String username, String password, String rol) {
        this.idUsuario = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public boolean validarLogin(String u, String p) {
        return username.equals(u) && password.equals(p);
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int id) { this.idUsuario = id; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getRol() { return rol; }
    public void setRol(String r) { this.rol = r; }
}
