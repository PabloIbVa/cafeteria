package com.cafeteria.pedidos;

import com.cafeteria.util.ConexionBD;
import com.cafeteria.model.Producto;

import java.sql.*;
import java.util.*;

public class PedidoDAO {

    // Obtiene id de usuario por correo si no está en sesión
    public int obtenerIdUsuarioPorCorreo(String correo) throws Exception {
        String sql = "SELECT id FROM usuarios WHERE correo = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
                else throw new Exception("Usuario no encontrado por correo: " + correo);
            }
        }
    }

    // Obtiene detalles de productos para un conjunto de IDs
    public Map<Integer, Producto> obtenerProductosPorIds(Set<Integer> ids) throws Exception {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id_producto, nombre, descripcion, costo, tiempo, src FROM productos WHERE id_producto IN (");
        StringJoiner sj = new StringJoiner(",");
        for (int id : ids) sj.add("?");
        sb.append(sj.toString()).append(")");
        String sql = sb.toString();

        Map<Integer, Producto> map = new HashMap<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            for (int id : ids) ps.setInt(i++, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setCosto(rs.getDouble("costo"));
                    p.setTiempo(rs.getInt("tiempo"));
                    p.setSrc(rs.getString("src"));
                    map.put(p.getId(), p);
                }
            }
        }
        return map;
    }

    // Crear pedido: recibe mapa idProducto->cantidad y el idUsuario, retorna id_pedido creado
    public int crearPedido(Map<Integer,Integer> carrito, int idUsuario) throws Exception {
        if (carrito == null || carrito.isEmpty()) throw new Exception("Carrito vacío");

        // 1. Obtener productos y calcular total y tiempo
        Map<Integer, Producto> productos = obtenerProductosPorIds(carrito.keySet());

        double precioTotal = 0;
        int tiempoTotal = 0;
        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
            int idProd = e.getKey();
            int cantidad = e.getValue();
            Producto p = productos.get(idProd);
            if (p == null) throw new Exception("Producto no encontrado: " + idProd);
            precioTotal += p.getCosto() * cantidad;
            tiempoTotal += p.getTiempo() * cantidad;
        }

        Connection con = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        ResultSet rsKeys = null;

        try {
            con = ConexionBD.getConnection();
            con.setAutoCommit(false);

            // Insertar en pedidos
            String sqlPedido = "INSERT INTO pedidos (id_usuario, fecha, precio_total, tiempo_total, estado) VALUES (?, CURDATE(), ?, ?, ?)";
            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setInt(1, idUsuario);
            psPedido.setInt(2, (int)Math.round(precioTotal)); // en tu esquema precio_total es int
            psPedido.setInt(3, tiempoTotal);
            psPedido.setString(4, "pendiente");
            psPedido.executeUpdate();

            rsKeys = psPedido.getGeneratedKeys();
            if (!rsKeys.next()) throw new Exception("No se obtuvo id_pedido generado");
            int idPedido = rsKeys.getInt(1);

            // Insertar detalle_pedidos
            String sqlDetalle = "INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, costo_u, costo_t) VALUES (?, ?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
                int idProd = e.getKey();
                int cantidad = e.getValue();
                Producto p = productos.get(idProd);
                double costoU = p.getCosto();
                double costoT = costoU * cantidad;

                psDetalle.setInt(1, idPedido);
                psDetalle.setInt(2, idProd);
                psDetalle.setInt(3, cantidad);
                psDetalle.setDouble(4, costoU);
                psDetalle.setDouble(5, costoT);
                psDetalle.addBatch();
            }
            psDetalle.executeBatch();

            con.commit();
            return idPedido;
        } catch (Exception ex) {
            if (con != null) try { con.rollback(); } catch (Exception e) {}
            throw ex;
        } finally {
            if (rsKeys != null) try { rsKeys.close(); } catch (Exception e) {}
            if (psDetalle != null) try { psDetalle.close(); } catch (Exception e) {}
            if (psPedido != null) try { psPedido.close(); } catch (Exception e) {}
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (Exception e) {}
        }
    }

    // Lista últimos N pedidos del usuario con 1) id_pedido, fecha, precio_total, estado, y 2) primer producto (imagen) y nombres concatenados (cortados fuera en front)
    public List<Map<String,Object>> listarUltimosPedidos(int idUsuario, int limit) throws Exception {
        String sql = "SELECT p.id_pedido, p.fecha, p.precio_total, p.estado " +
                     "FROM pedidos p WHERE p.id_usuario = ? ORDER BY p.id_pedido DESC LIMIT ?";
        List<Map<String,Object>> salida = new ArrayList<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPedido = rs.getInt("id_pedido");
                    Map<String,Object> fila = new HashMap<>();
                    fila.put("id_pedido", idPedido);
                    fila.put("fecha", rs.getDate("fecha").toString());
                    fila.put("precio_total", rs.getInt("precio_total"));
                    fila.put("estado", rs.getString("estado"));

                    // Obtener primer producto del pedido
                    String sqlDetalle = "SELECT dp.id_producto, dp.cantidad, pr.nombre, pr.src " +
                                        "FROM detalle_pedidos dp JOIN productos pr ON dp.id_producto = pr.id_producto " +
                                        "WHERE dp.id_pedido = ? LIMIT 1";
                    try (PreparedStatement ps2 = con.prepareStatement(sqlDetalle)) {
                        ps2.setInt(1, idPedido);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            if (rs2.next()) {
                                fila.put("primer_producto_nombre", rs2.getString("nombre"));
                                fila.put("primer_producto_img", rs2.getString("src"));
                            } else {
                                fila.put("primer_producto_nombre", "");
                                fila.put("primer_producto_img", "");
                            }
                        }
                    }

                    // Obtener nombres concatenados para mostrar (limitamos en front)
                    String sqlNombres = "SELECT GROUP_CONCAT(pr.nombre SEPARATOR ', ') as nombres " +
                                        "FROM detalle_pedidos dp JOIN productos pr ON dp.id_producto = pr.id_producto " +
                                        "WHERE dp.id_pedido = ?";
                    try (PreparedStatement ps3 = con.prepareStatement(sqlNombres)) {
                        ps3.setInt(1, idPedido);
                        try (ResultSet rs3 = ps3.executeQuery()) {
                            if (rs3.next()) fila.put("nombres", rs3.getString("nombres"));
                            else fila.put("nombres", "");
                        }
                    }

                    salida.add(fila);
                }
            }
        }
        return salida;
    }
}
