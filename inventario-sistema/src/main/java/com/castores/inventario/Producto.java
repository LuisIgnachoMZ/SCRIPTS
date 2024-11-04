package com.castores.inventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class Producto {
    private int idProducto;
    private String nombre;
    private int precio;
    private int cantidad; 
    private boolean activo;
    private Connection conexion;

    // Constructor que incluye cantidad
    public Producto(int idProducto, String nombre, int precio, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad; 
        this.activo = true;
        conectarBaseDeDatos();
    }
    
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

    public int save() {
        if (conexion == null) {
            System.err.println("Error: La conexión a la base de datos no se ha establecido.");
            return -1; // Indica un error si no hay conexión
        }

        String sql = "INSERT INTO producto (nombre, precio) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.nombre);
            ps.setInt(2, this.precio);
            ps.executeUpdate();
            System.out.println("Producto guardado en la base de datos.");

            // Obtener el ID generado
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retorna el ID del producto insertado
                } else {
                    System.err.println("Error: No se generó ningún ID para el producto.");
                    return -1; // Indica un error si no se generó el ID
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar el producto en la base de datos: " + e.getMessage());
            return -1; // Indica un error en caso de excepción
        }
    }
    
    public List<Producto> obtenerProductosActivos() {
        List<Producto> productosActivos = new ArrayList<>();
        String sql = "SELECT p.producto_id, p.nombre, p.precio, " +
                    "COALESCE(SUM(CASE WHEN i.tipo_movimiento = 'entrada' OR i.tipo_movimiento = 'nuevo' THEN i.cantidad ELSE 0 END), 0) - " +
                    "COALESCE(SUM(CASE WHEN i.tipo_movimiento = 'salida' THEN i.cantidad ELSE 0 END), 0) AS cantidad_total " +
                    "FROM inventario_sistema.producto p " +
                    "LEFT JOIN inventario_sistema.inventario i ON i.producto_id = p.producto_id " +
                    "WHERE p.estatus = 1 " +
                    "GROUP BY p.producto_id, p.nombre, p.precio";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idProducto = rs.getInt("producto_id");
                String nombre = rs.getString("nombre");
                int precio = rs.getInt("precio"); // Ajustado a BigDecimal para precios
                int cantidad = rs.getInt("cantidad_total"); // Cambiado a cantidad_total
                productosActivos.add(new Producto(idProducto, nombre, precio, cantidad)); 
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos activos: " + e.getMessage());
        }
        return productosActivos;
    }

    public List<Producto> obtenerProductosInactivos() {
        List<Producto> productosInactivos = new ArrayList<>();
        String sql = "SELECT p.producto_id, p.nombre, p.precio, i.cantidad " +
                    "FROM inventario_sistema.producto p " +
                    "JOIN inventario_sistema.inventario i ON i.producto_id = p.producto_id " +
                    "WHERE estatus = 0";  // Cambia el filtro a 'estatus = 0' para productos inactivos

        try (PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idProducto = rs.getInt("producto_id");
                String nombre = rs.getString("nombre");
                int precio = rs.getInt("precio");
                int cantidad = rs.getInt("cantidad"); 
                productosInactivos.add(new Producto(idProducto, nombre, precio, cantidad)); 
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los productos inactivos: " + e.getMessage());
        }
        return productosInactivos;
    }

    public boolean darDeBaja(int idProducto) {
        String sql = "UPDATE producto SET estatus = 0 WHERE producto_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se modificó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al dar de baja el producto: " + e.getMessage());
            return false; // Retorna false si ocurrió un error
        }
    }

    public boolean reactivarProducto(int idProducto) {
        String sql = "UPDATE producto SET estatus = 1 WHERE producto_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se modificó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al reactivar el producto: " + e.getMessage());
            return false; // Retorna false si ocurrió un error
        }
    }

    // Métodos getter
    public int getId() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public int getCantidad() { 
        return cantidad;
    }

    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
    }
}
