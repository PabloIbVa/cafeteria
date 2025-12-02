package com.cafeteria.pedidos;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/ObtenerDetallePedidoServlet")
public class ObtenerDetallePedidoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar sesión
        HttpSession ses = request.getSession(false);
        if (ses == null || (ses.getAttribute("id") == null && ses.getAttribute("correo") == null)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int idPedido = Integer.parseInt(idStr);
            PedidoDAO dao = new PedidoDAO();
            
            // Obtenemos cabecera
            Map<String, Object> cabecera = dao.obtenerPedidoPorId(idPedido);
            
            if (cabecera == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Obtenemos lista de productos
            List<Map<String, Object>> detalles = dao.obtenerDetallesPedido(idPedido);

            // Creamos respuesta conjunta
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("pedido", cabecera);
            respuesta.put("detalles", detalles);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Gson().toJson(respuesta));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}