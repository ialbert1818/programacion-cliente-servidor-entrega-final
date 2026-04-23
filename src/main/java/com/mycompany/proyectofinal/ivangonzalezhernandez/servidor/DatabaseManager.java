package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import java.sql.*;

/**
 * Gestor de conexion a Apache Derby (Java DB).
 * Crea el schema y tablas si no existen.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:derby:FacturacionDB;create=true";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        inicializarTablas();
    }

    public static DatabaseManager getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

    private void inicializarTablas() throws SQLException {
        try (Statement st = connection.createStatement()) {

            // Tabla USUARIOS
            if (!tablaExiste("USUARIOS")) {
                st.executeUpdate(
                    "CREATE TABLE USUARIOS (" +
                    "  ID_USUARIO   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "  USERNAME     VARCHAR(50) UNIQUE NOT NULL," +
                    "  PASSWORD     VARCHAR(100) NOT NULL," +
                    "  ROL          VARCHAR(20) NOT NULL" +
                    ")"
                );
                // Usuario por defecto
                st.executeUpdate(
                    "INSERT INTO USUARIOS (USERNAME, PASSWORD, ROL) VALUES ('admin','1234','ADMIN')"
                );
                st.executeUpdate(
                    "INSERT INTO USUARIOS (USERNAME, PASSWORD, ROL) VALUES ('vendedor','vendedor','VENDEDOR')"
                );
            }

            // Tabla CLIENTES
            if (!tablaExiste("CLIENTES")) {
                st.executeUpdate(
                    "CREATE TABLE CLIENTES (" +
                    "  ID_CLIENTE  INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "  NOMBRE      VARCHAR(100) NOT NULL," +
                    "  CORREO      VARCHAR(100) NOT NULL" +
                    ")"
                );
            }

            // Tabla PRODUCTOS
            if (!tablaExiste("PRODUCTOS")) {
                st.executeUpdate(
                    "CREATE TABLE PRODUCTOS (" +
                    "  ID_PRODUCTO  INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "  NOMBRE       VARCHAR(100) NOT NULL," +
                    "  PRECIO       DOUBLE NOT NULL," +
                    "  CANTIDAD     INT NOT NULL," +
                    "  TIPO         VARCHAR(20) NOT NULL" +
                    ")"
                );
            }

            // Tabla FACTURAS
            if (!tablaExiste("FACTURAS")) {
                st.executeUpdate(
                    "CREATE TABLE FACTURAS (" +
                    "  ID_FACTURA   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "  ID_CLIENTE   INT NOT NULL REFERENCES CLIENTES(ID_CLIENTE)," +
                    "  FECHA        DATE NOT NULL," +
                    "  TOTAL        DOUBLE NOT NULL" +
                    ")"
                );
            }

            // Tabla DETALLE_FACTURA
            if (!tablaExiste("DETALLE_FACTURA")) {
                st.executeUpdate(
                    "CREATE TABLE DETALLE_FACTURA (" +
                    "  ID_DETALLE   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "  ID_FACTURA   INT NOT NULL REFERENCES FACTURAS(ID_FACTURA)," +
                    "  ID_PRODUCTO  INT NOT NULL REFERENCES PRODUCTOS(ID_PRODUCTO)," +
                    "  CANTIDAD     INT NOT NULL," +
                    "  SUBTOTAL     DOUBLE NOT NULL" +
                    ")"
                );
            }

            System.out.println("[DB] Tablas inicializadas correctamente.");
        }
    }

    private boolean tablaExiste(String tabla) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, tabla, new String[]{"TABLE"});
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            // Apagar Derby limpiamente
            try { DriverManager.getConnection("jdbc:derby:;shutdown=true"); }
            catch (SQLException ignore) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
