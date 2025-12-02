package com.cafeteria.pedidos;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/CrearPedido")
public class CrearPedidoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession ses = request.getSession(false);
        if (ses == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<Integer,Integer> carrito = (Map<Integer,Integer>) ses.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Carrito vacío\"}");
            return;
        }

        try {
            int idUsuario = -1;
            if (ses.getAttribute("id") != null) {
                idUsuario = (Integer) ses.getAttribute("id");
            } else if (ses.getAttribute("correo") != null) {
                String correo = (String) ses.getAttribute("correo");
                PedidoDAO dao = new PedidoDAO();
                idUsuario = dao.obtenerIdUsuarioPorCorreo(correo);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            PedidoDAO dao = new PedidoDAO();
            int idPedido = dao.crearPedido(carrito, idUsuario);

            // limpiar carrito
            ses.removeAttribute("carrito");

            Map<String,Object> resp = new HashMap<>();
            resp.put("ok", true);
            resp.put("id_pedido", idPedido);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Gson().toJson(resp));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }
}
