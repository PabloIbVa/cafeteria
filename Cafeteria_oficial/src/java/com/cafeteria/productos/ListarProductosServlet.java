/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.cafeteria.productos;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import com.google.gson.Gson;

import com.cafeteria.model.Producto;
import com.cafeteria.dao.ProductoDAO;

@WebServlet("/ListarProductosServlet")
public class ListarProductosServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            List<Producto> lista = new ProductoDAO().listarTodos();
            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(lista));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

