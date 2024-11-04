package com.castores.inventario;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter; 
import jakarta.servlet.RequestDispatcher; 
import java.util.List;

public class ProductoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configura la codificación de la solicitud
        request.setCharacterEncoding("UTF-8");
        // Establece el tipo de contenido de la respuesta como texto HTML
        response.setContentType("text/html; charset=UTF-8");
        
        // Redirigir a producto.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("producto.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener el valor de cmd
        String cmd = request.getParameter("cmd");

        // Preparar respuesta
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Ejecutar la acción según el valor de cmd
        if ("iniciar_sesion".equals(cmd)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            Usuario usuario = new Usuario();
            List<Permiso> permisos = usuario.iniciarSesion(username, password);

            if (permisos == null) {
                // Error en la conexión o consulta SQL
                out.println("{\"error\": \"Error al conectar con la base de datos\"}");
            } else if (permisos.isEmpty()) {
                // Credenciales incorrectas
                out.println("{\"error\": \"Usuario o contraseña incorrectos\"}");
            } else {
                // Generar JSON de permisos
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < permisos.size(); i++) {
                    Permiso permiso = permisos.get(i);
                    json.append("{\"permisoId\": ").append(permiso.getIdPermiso())
                        .append(", \"nombre\": \"").append(permiso.getNombre()).append("\"}");
                    if (i < permisos.size() - 1) {
                        json.append(", ");
                    }
                }
                json.append("]");
                out.println("{\"success\": true, \"idUsuario\": " + usuario.getIdUsuario() + ", \"permisos\": " + json.toString() + "}");
            }
        }
        else if ("registrar_producto".equals(cmd)) {
            // Lógica para registrar el producto
            String nombre = request.getParameter("nombre");
            int precio = Integer.parseInt(request.getParameter("precio"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));

            // Crear el producto y delegar el guardado al modelo
            Producto producto = new Producto(0, nombre, precio, 0); 
            int idProducto = producto.save(); 

            if (idProducto != -1) {
                String tipoMovimiento = "Nuevo"; // Definir el tipo de movimiento
                Inventario inventario = new Inventario(idProducto, 1, cantidad, tipoMovimiento, "", "", "");
                boolean movimientoRegistrado = inventario.registrarMovimiento();

                if (movimientoRegistrado) {
                    out.println("{\"message\": \"Producto y movimiento registrados exitosamente\"}");
                } else {
                    out.println("{\"error\": \"Producto registrado, pero hubo un error al registrar el movimiento en el inventario\"}");
                }
            } else {
                out.println("{\"error\": \"Error al guardar el producto. Intenta nuevamente.\"}");
            }

        } else if ("obtener_salidas".equals(cmd)) {
            // Lógica para obtener productos activos
            Producto producto = new Producto(0, "", 0, 0); // Crear un objeto de Producto para acceder al método
            List<Producto> productosActivos = producto.obtenerProductosActivos();

            // Convertir la lista a JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < productosActivos.size(); i++) {
                Producto p = productosActivos.get(i);
                json.append("{\"nombre\": \"").append(p.getNombre()).append("\", ")
                    .append("\"precio\": ").append(p.getPrecio()).append(", ")
                    .append("\"cantidad\": ").append(p.getCantidad()).append(", ") 
                    .append("\"idProducto\": ").append(p.getId()).append("}"); 
                if (i < productosActivos.size() - 1) {
                    json.append(", ");
                }
            }
            json.append("]");

            out.println(json.toString());

        } else if ("obtener_inactivos".equals(cmd)) {
            // Lógica para obtener productos inactivos
            Producto producto = new Producto(0, "", 0, 0); // Crear un objeto de Producto para acceder al método
            List<Producto> productosInactivos = producto.obtenerProductosInactivos();

            // Convertir la lista a JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < productosInactivos.size(); i++) {
                Producto p = productosInactivos.get(i);
                json.append("{\"nombre\": \"").append(p.getNombre()).append("\", ")
                    .append("\"precio\": ").append(p.getPrecio()).append(", ")
                    .append("\"cantidad\": ").append(p.getCantidad()).append(", ")
                    .append("\"idProducto\": ").append(p.getId()).append("}");
                if (i < productosInactivos.size() - 1) {
                    json.append(", ");
                }
            }
            json.append("]");

            out.println(json.toString());
        } else if ("dar_de_baja".equals(cmd)) {
            // Lógica para dar de baja el producto
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));

            Producto producto = new Producto(idProducto, "", 0, 0); // Crear el objeto con el idProducto
            boolean exito = producto.darDeBaja(idProducto);

            if (exito) {
                out.println("{\"message\": \"Producto dado de baja exitosamente\"}");
            } else {
                out.println("{\"error\": \"Error al dar de baja el producto. Intenta nuevamente.\"}");
            }
        } else if ("reactivar_producto".equals(cmd)) {
            // Lógica para reactivar el producto
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));

            Producto producto = new Producto(idProducto, "", 0, 0); // Crear el objeto con el idProducto
            boolean exito = producto.reactivarProducto(idProducto);

            if (exito) {
                out.println("{\"message\": \"Producto reactivado exitosamente\"}");
            } else {
                out.println("{\"error\": \"Error al reactivar el producto. Intenta nuevamente.\"}");
            }
        } else if ("obtener_movimientos_inventario".equals(cmd)) {
            Inventario inventario = new Inventario(0, 0, 0, "", "", "", ""); // Crear un objeto Inventario para acceder al método
            List<Inventario> movimientosInventario = inventario.obtenerMovimientosInventarioConUsuario();

            // Convertir la lista de movimientos a JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < movimientosInventario.size(); i++) {
                Inventario m = movimientosInventario.get(i);
                json.append("{\"productoId\": ").append(m.getProductoId()).append(", ")
                    .append("\"nombreUsuario\": \"").append(m.getNombreUsuario()).append("\", ")
                    .append("\"cantidad\": ").append(m.getCantidad()).append(", ")
                    .append("\"tipoMovimiento\": \"").append(m.getTipoMovimiento()).append("\", ")
                    .append("\"fecha\": \"").append(m.getFechaMovimiento()).append("\", ")
                    .append("\"nombreProducto\": \"").append(m.getNombreProducto()).append("\"}"); // Agregando el campo "fecha"

                if (i < movimientosInventario.size() - 1) {
                    json.append(", ");
                }
            }
            json.append("]");

            out.println(json.toString());
        } else if ("registrar_entrada".equals(cmd)) {
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));
            String tipoMovimiento = "Entrada"; // Definir el tipo de movimiento
            Inventario inventario = new Inventario(idProducto, idUsuario, cantidad, tipoMovimiento, "", "", "");
            boolean movimientoRegistrado = inventario.registrarMovimiento();

            if (movimientoRegistrado) {
                out.println("{\"message\": \"Movimiento registrados exitosamente\"}");
            } else {
                out.println("{\"error\": \"Hubo un error al registrar el movimiento en el inventario\"}");
            }
        } else if ("registrar_salida".equals(cmd)) {
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));
            String tipoMovimiento = "Salida"; // Definir el tipo de movimiento
            Inventario inventario = new Inventario(idProducto, idUsuario, cantidad, tipoMovimiento, "", "", "");
            boolean movimientoRegistrado = inventario.registrarMovimiento();

            if (movimientoRegistrado) {
                out.println("{\"message\": \"Movimiento registrados exitosamente\"}");
            } else {
                out.println("{\"error\": \"Hubo un error al registrar el movimiento en el inventario\"}");
            }
        }
        else {
            out.println("{\"error\": \"Comando no reconocido: " + cmd + "\"}");
        }
        
    }
}
