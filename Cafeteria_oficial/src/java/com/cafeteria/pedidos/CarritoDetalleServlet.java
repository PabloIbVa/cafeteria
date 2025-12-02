package com.cafeteria.pedidos;

import com.cafeteria.model.Producto;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/CarritoDetalle")
public class CarritoDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession ses = request.getSession(false);
        if (ses == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<Integer, Integer> carrito = (Map<Integer,Integer>) ses.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            // devolver estructura vacía con total 0
            Map<String,Object> resp = new HashMap<>();
            resp.put("productos", new ArrayList<>());
            resp.put("total", 0);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Gson().toJson(resp));
            return;
        }

        try {
            PedidoDAO dao = new PedidoDAO();
            Map<Integer,Producto> prodInfo = dao.obtenerProductosPorIds(carrito.keySet());
            List<Map<String,Object>> lista = new ArrayList<>();
            double total = 0;
            for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
                int id = e.getKey();
                int qty = e.getValue();
                Producto p = prodInfo.get(id);
                if (p == null) continue;
                double subtotal = p.getCosto() * qty;
                total += subtotal;
                Map<String,Object> item = new HashMap<>();
                item.put("id", id);
                item.put("nombre", p.getNombre());
                item.put("descripcion", p.getDescripcion());
                item.put("cantidad", qty);
                item.put("precio_unitario", p.getCosto());
                item.put("subtotal", subtotal);
                item.put("src", p.getSrc());
                lista.add(item);
            }
            Map<String,Object> resp = new HashMap<>();
            resp.put("productos", lista);
            resp.put("total", total);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Gson().toJson(resp));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
