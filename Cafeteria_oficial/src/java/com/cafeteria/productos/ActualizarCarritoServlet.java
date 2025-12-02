/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.cafeteria.productos;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@WebServlet("/ActualizarCarritoServlet")
public class ActualizarCarritoServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int idProducto = Integer.parseInt(request.getParameter("id"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));

        HttpSession sesion = request.getSession();
        Map<Integer, Integer> carrito = (Map<Integer, Integer>) sesion.getAttribute("carrito");

        if (carrito == null) {
            carrito = new HashMap<>();
        }

        if (cantidad > 0) {
            carrito.put(idProducto, cantidad);
        } else {
            carrito.remove(idProducto);
        }

        sesion.setAttribute("carrito", carrito);
        response.getWriter().write("OK");
    }
}

