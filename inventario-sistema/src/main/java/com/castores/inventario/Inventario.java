package com.castores.inventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Inventario {
    private int productoId;
    private int usuarioId;
    private int cantidad;
    private String tipoMovimiento;
    private String nombreUsuario;
    private String fechaMovimiento;
    private String nombreProducto;
    private Connection conexion;

    // Constructor
    public Inventario(int productoId, int usuarioId, int cantidad, String tipoMovimiento, String nombreUsuario, String fechaMovimiento, String nombreProducto) {
        this.productoId = productoId;
        this.usuarioId = usuarioId;
        this.cantidad = cantidad;
        this.tipoMovimiento = tipoMovimiento;
        this.nombreUsuario = nombreUsuario;
        this.fechaMovimiento = fechaMovimiento;
        this.nombreProducto = nombreProducto;
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

    // Método para registrar un movimiento (entrada o salida) en el inventario
    public boolean registrarMovimiento() {
        if (conexion == null) {
            System.err.println("Error: La conexión a la base de datos no se ha establecido.");
            return false;
        }

        String sql = "INSERT INTO inventario (producto_id, usuario_id, cantidad, tipo_movimiento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, this.productoId);
            ps.setInt(2, this.usuarioId);
            ps.setInt(3, this.cantidad);
            ps.setString(4, this.tipoMovimiento);
            ps.executeUpdate();
            System.out.println("Movimiento de inventario registrado.");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el movimiento de inventario: " + e.getMessage());
            return false;
        }
    }

    public List<Inventario> obtenerMovimientosInventarioConUsuario() {
        List<Inventario> movimientosInventario = new ArrayList<>();
        String sql = "SELECT i.producto_id, i.usuario_id, u.nombre AS nombre_usuario, p.nombre AS nombre_producto, " +
                    "i.cantidad, i.tipo_movimiento, i.fecha_movimiento " +
                    "FROM inventario_sistema.inventario i " +
                    "JOIN inventario_sistema.usuario u ON i.usuario_id = u.usuario_id " +
                    "JOIN inventario_sistema.producto p ON p.producto_id = i.producto_id AND p.estatus = 1";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try (PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                int usuarioId = rs.getInt("usuario_id");
                String nombreUsuario = rs.getString("nombre_usuario");
                String nombreProducto = rs.getString("nombre_producto"); // Obtener nombre del producto
                int cantidad = rs.getInt("cantidad");
                String tipoMovimiento = rs.getString("tipo_movimiento");

                // Extraer y formatear la fecha
                Date fechaMovimiento = rs.getTimestamp("fecha_movimiento");
                String fechaMovimientoFormateada = dateFormat.format(fechaMovimiento);

                Inventario movimiento = new Inventario(productoId, usuarioId, cantidad, tipoMovimiento, nombreUsuario, fechaMovimientoFormateada, nombreProducto);
                movimientosInventario.add(movimiento);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los movimientos de inventario: " + e.getMessage());
        }

        return movimientosInventario;
    }

    // Getters y Setters
    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int productoId) {
        this.usuarioId = usuarioId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
