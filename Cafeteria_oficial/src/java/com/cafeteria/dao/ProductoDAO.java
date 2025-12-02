package com.cafeteria.dao;

import com.cafeteria.model.Producto;
import com.cafeteria.util.ConexionBD;
import java.sql.*;
import java.util.*;

public class ProductoDAO {

    public List<Producto> listarTodos() throws Exception {
        List<Producto> lista = new ArrayList<>();
        Connection con = ConexionBD.getConnection();
        String sql = "SELECT * FROM productos WHERE estado = 1";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Producto p = new Producto();
            p.setId(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setCosto(rs.getDouble("costo"));
            p.setTiempo(rs.getInt("tiempo"));
            p.setArea(rs.getString("area"));
            p.setSrc(rs.getString("src"));
            p.setEstado(rs.getInt("estado"));
            lista.add(p);
        }
        return lista;
    }

    public List<Producto> filtrar(String area, double precioMax, int tiempoMax, String search) throws Exception {
        List<Producto> lista = new ArrayList<>();
        Connection con = ConexionBD.getConnection();

        String sql = "SELECT * FROM productos WHERE estado = 1 ";
        if (area != null && !area.equals("")) sql += " AND area = ?";
        if (precioMax > 0) sql += " AND costo <= ?";
        if (tiempoMax > 0) sql += " AND tiempo <= ?";
        if (search != null && !search.equals("")) sql += " AND nombre LIKE ?";

        PreparedStatement ps = con.prepareStatement(sql);

        int i = 1;
        if (area != null && !area.equals("")) ps.setString(i++, area);
        if (precioMax > 0) ps.setDouble(i++, precioMax);
        if (tiempoMax > 0) ps.setInt(i++, tiempoMax);
        if (search != null && !search.equals("")) ps.setString(i++, "%" + search + "%");

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Producto p = new Producto();
            p.setId(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setCosto(rs.getDouble("costo"));
            p.setTiempo(rs.getInt("tiempo"));
            p.setArea(rs.getString("area"));
            p.setSrc(rs.getString("src"));
            p.setEstado(rs.getInt("estado"));
            lista.add(p);
        }
        return lista;
    }
    
    public List<Producto> listarTodoAdmin() throws Exception {
        List<Producto> lista = new ArrayList<>();
        Connection con = ConexionBD.getConnection();
        // Quitamos el WHERE estado = 1 para que traiga todo
        String sql = "SELECT * FROM productos ORDER BY id_producto DESC"; 
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Producto p = new Producto();
            p.setId(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setCosto(rs.getDouble("costo"));
            p.setTiempo(rs.getInt("tiempo"));
            p.setArea(rs.getString("area"));
            p.setSrc(rs.getString("src"));
            p.setEstado(rs.getInt("estado"));
            lista.add(p);
        }
        con.close();
        return lista;
    }

    public void guardarProducto(Producto p) throws Exception {
        Connection con = ConexionBD.getConnection();
        String sql;
        PreparedStatement ps;

        // Si el ID es 0, es NUEVO -> INSERT
        if (p.getId() == 0) {
            sql = "INSERT INTO productos (nombre, descripcion, costo, tiempo, area, src, estado) VALUES (?, ?, ?, ?, ?, ?, 1)";
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getCosto());
            ps.setInt(4, p.getTiempo());
            ps.setString(5, p.getArea());
            ps.setString(6, p.getSrc());
        } else {
            // Si tiene ID, es MODIFICAR -> UPDATE
            sql = "UPDATE productos SET nombre=?, descripcion=?, costo=?, tiempo=?, area=?, src=? WHERE id_producto=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getCosto());
            ps.setInt(4, p.getTiempo());
            ps.setString(5, p.getArea());
            ps.setString(6, p.getSrc());
            ps.setInt(7, p.getId());
        }
        ps.executeUpdate();
        con.close();
    }

    public void cambiarEstado(int idProducto, int nuevoEstado) throws Exception {
        Connection con = ConexionBD.getConnection();
        String sql = "UPDATE productos SET estado = ? WHERE id_producto = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, nuevoEstado);
        ps.setInt(2, idProducto);
        ps.executeUpdate();
        con.close();
    }
}
