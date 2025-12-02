package com.cafeteria.productos;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import com.cafeteria.model.Producto;
import com.cafeteria.dao.ProductoDAO;

@WebServlet("/FiltrarProductos")
public class FiltrarProductosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Parámetros que recibe desde AJAX
        String area = request.getParameter("area");
        String precio = request.getParameter("precio");
        String tiempo = request.getParameter("tiempo");
        String search = request.getParameter("search");

        double precioMax = (precio != null && !precio.equals("")) ? Double.parseDouble(precio) : 0;
        int tiempoMax = (tiempo != null && !tiempo.equals("")) ? Integer.parseInt(tiempo) : 0;

        List<Producto> productos = null;
        try {
            productos = new ProductoDAO().filtrar(area, precioMax, tiempoMax, search);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = gson.toJson(productos);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}



