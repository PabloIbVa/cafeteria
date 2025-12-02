package com.cafeteria.productos;

import com.cafeteria.dao.ProductoDAO;
import com.cafeteria.model.Producto;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminAlimentosServlet")
public class AdminAlimentosServlet extends HttpServlet {

    // GET: Para obtener la lista de productos (JSON)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            List<Producto> lista = new ProductoDAO().listarTodoAdmin();
            response.getWriter().write(new Gson().toJson(lista));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    // POST: Para Guardar (Nuevo/Editar) o Cambiar Estado
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        try {
            ProductoDAO dao = new ProductoDAO();

            if ("guardar".equals(accion)) {
                // Recibir datos del formulario
                String idStr = request.getParameter("id");
                int id = (idStr == null || idStr.isEmpty()) ? 0 : Integer.parseInt(idStr);
                
                Producto p = new Producto();
                p.setId(id);
                p.setNombre(request.getParameter("nombre"));
                p.setDescripcion(request.getParameter("descripcion"));
                p.setCosto(Double.parseDouble(request.getParameter("precio")));
                p.setTiempo(Integer.parseInt(request.getParameter("tiempo")));
                p.setArea(request.getParameter("area"));
                p.setSrc(request.getParameter("src"));

                dao.guardarProducto(p);
                response.getWriter().write("OK");

            } else if ("estado".equals(accion)) {
                // Eliminar o Reactivar
                int id = Integer.parseInt(request.getParameter("id"));
                int estado = Integer.parseInt(request.getParameter("estado")); // 1 o 0
                dao.cambiarEstado(id, estado);
                response.getWriter().write("OK");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}