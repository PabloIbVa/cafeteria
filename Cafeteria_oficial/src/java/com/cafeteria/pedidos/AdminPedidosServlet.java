package com.cafeteria.pedidos;

import com.cafeteria.pedidos.PedidoDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminPedidosServlet")
public class AdminPedidosServlet extends HttpServlet {

    // GET: Obtener la lista de pedidos ordenada
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            PedidoDAO dao = new PedidoDAO();
            List<Map<String, Object>> lista = dao.listarPedidosAdmin();
            response.getWriter().write(new Gson().toJson(lista));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    // POST: Actualizar el estado
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String idStr = request.getParameter("id");
        String estado = request.getParameter("estado");

        if (idStr == null || estado == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            PedidoDAO dao = new PedidoDAO();
            dao.actualizarEstadoPedido(id, estado);
            response.getWriter().write("OK");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        }
    }
}