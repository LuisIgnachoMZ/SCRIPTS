package com.castores.inventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasenia;
    private int rolId;
    private Connection conexion;

    // Constructor que incluye la conexión
    public Usuario() {
        conectarBaseDeDatos();
    }

    // Método para conectar a la base de datos
    private void conectarBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/inventario_sistema";
        String usuario = "root";
        String contraseña = "castores";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión a MySQL exitosa.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    // Método para validar credenciales y obtener los permisos
    public List<Permiso> iniciarSesion(String username, String password) {
        if (conexion == null) {
            System.err.println("Error: La conexión a la base de datos no se ha establecido.");
            return null; // Indica un error de conexión
        }

        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT usuario_id, rol_id FROM usuario WHERE nombre = ? AND contrasenia = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                this.idUsuario = rs.getInt("usuario_id");
                this.rolId = rs.getInt("rol_id");
                permisos = obtenerPermisos();
            }
        } catch (SQLException e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            return null; // Indica un error en caso de excepción SQL
        }
        return permisos;
    }

    // Método para obtener permisos basado en rol_id
    private List<Permiso> obtenerPermisos() {
        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT p.permiso_id, p.nombre, p.estatus " +
                     "FROM rol_permiso rp " +
                     "JOIN permiso p ON rp.permiso_id = p.permiso_id " +
                     "WHERE rp.rol_id = ? AND rp.estatus = 1 AND p.estatus = 1";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, this.rolId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int permisoId = rs.getInt("permiso_id");
                String nombrePermiso = rs.getString("nombre");
                int estatus = rs.getInt("estatus");
                permisos.add(new Permiso(permisoId, nombrePermiso, estatus));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener permisos: " + e.getMessage());
        }
        return permisos;
    }

    // Getters para Usuario
    public int getIdUsuario() {
        return idUsuario;
    }

    public int getRolId() {
        return rolId;
    }
}
