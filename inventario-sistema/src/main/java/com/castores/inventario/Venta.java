package com.castores.inventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class Venta {
    private int ventaId;
    private int productoId;
    private int cantidad;
    private int estatus;
    private Connection conexion;

    // Constructor principal
    public Venta(int productoId, int cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estatus = 1; // Estatus activo por defecto
        conectarBaseDeDatos();
    }

    // Constructor para cargar venta existente
    public Venta(int ventaId, int productoId, int cantidad, int estatus) {
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estatus = estatus;
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

    // Método para guardar la venta en la base de datos
    public boolean save() {
        if (conexion == null) {
            System.err.println("Error: La conexión a la base de datos no se ha establecido.");
            return false;
        }

        String sql = "INSERT INTO venta (producto_id, cantidad, estatus) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, this.productoId);
            ps.setInt(2, this.cantidad);
            ps.setInt(3, this.estatus);
            ps.executeUpdate();
            System.out.println("Venta guardada en la base de datos.");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar la venta en la base de datos: " + e.getMessage());
            return false;
        }
    }

    // Método para obtener ventas activas
    public List<Venta> obtenerVentasActivas() {
        List<Venta> ventasActivas = new ArrayList<>();
        String sql = "SELECT venta_id, producto_id, cantidad, estatus FROM venta WHERE estatus = 1";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int ventaId = rs.getInt("venta_id");
                int productoId = rs.getInt("producto_id");
                int cantidad = rs.getInt("cantidad");
                int estatus = rs.getInt("estatus");
                ventasActivas.add(new Venta(ventaId, productoId, cantidad, estatus));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las ventas activas: " + e.getMessage());
        }
        return ventasActivas;
    }

    // Métodos getter
    public int getVentaId() {
        return ventaId;
    }

    public int getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getEstatus() {
        return estatus;
    }

    // Método para dar de baja una venta (cambia estatus a 0)
    public boolean darDeBaja() {
        if (conexion == null) {
            System.err.println("Error: La conexión a la base de datos no se ha establecido.");
            return false;
        }

        String sql = "UPDATE venta SET estatus = 0 WHERE venta_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, this.ventaId);
            ps.executeUpdate();
            this.estatus = 0; // Actualizar estatus en el objeto
            System.out.println("Venta dada de baja exitosamente.");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al dar de baja la venta: " + e.getMessage());
            return false;
        }
    }
}
