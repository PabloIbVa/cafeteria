package com.cafeteria.sesion;

import com.cafeteria.util.ConexionBD;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("password");

        if (correo == null || contrasena == null || correo.trim().isEmpty()) {
            response.sendRedirect("index.html?error=missing");
            return;
        }

        try (Connection conn = ConexionBD.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id, matricula, nombre, apellidos, correo, rol FROM usuarios WHERE correo = ? AND contrasena = ?"
            );
            ps.setString(1, correo);
            ps.setString(2, contrasena); // si usas hash, comparar con hash
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession(true); // crea sesión si no existe
                session.setAttribute("idUsuario", rs.getInt("id"));
                session.setAttribute("matricula", rs.getString("matricula"));
                session.setAttribute("nombre", rs.getString("nombre"));
                session.setAttribute("apellidos", rs.getString("apellidos"));
                session.setAttribute("correo", rs.getString("correo"));
                session.setAttribute("rol", rs.getString("rol"));

                // Redirige a perfil.jsp (JSP porque lee session)
                response.sendRedirect("principal.html");
            } else {
                response.sendRedirect("index.html?error=invalid");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("index.html?error=db");
        }
    }
}
