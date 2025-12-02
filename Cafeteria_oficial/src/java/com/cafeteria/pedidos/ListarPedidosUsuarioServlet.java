package com.cafeteria.pedidos;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/ListarPedidosUsuarioServlet")
public class ListarPedidosUsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession ses = request.getSession(false);
        if (ses == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int idUsuario = -1;
            if (ses.getAttribute("id") != null) {
                idUsuario = (Integer) ses.getAttribute("id");
            } else if (ses.getAttribute("correo") != null) {
                PedidoDAO dao = new PedidoDAO();
                idUsuario = dao.obtenerIdUsuarioPorCorreo((String) ses.getAttribute("correo"));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // MODIFICACIÓN: Leer parámetro "limit"
            int limit = 3; // Default para el carrito
            String limitParam = request.getParameter("limit");
            if (limitParam != null && !limitParam.isEmpty()) {
                try {
                    limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException e) {
                    limit = 3;
                }
            }

            PedidoDAO dao = new PedidoDAO();
            // Llamamos al DAO con el límite dinámico
            List<Map<String,Object>> pedidos = dao.listarUltimosPedidos(idUsuario, limit);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Gson().toJson(pedidos));
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}