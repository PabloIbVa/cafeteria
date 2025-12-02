package com.cafeteria.sesion;

import com.cafeteria.util.ConexionBD;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String matricula = request.getParameter("matricula");
        String nombre = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("password");

        if (matricula == null || nombre == null || apellidos == null ||
                correo == null || contrasena == null ||
                matricula.trim().isEmpty() || correo.trim().isEmpty() ) {
            // Redirige a la página de registro indicando error
            response.sendRedirect("registro.html?error=missing");
            return;
        }

        matricula = matricula.trim();
        // Asignar rol según prefijo de matrícula
        String rol;
        if (matricula.startsWith("10")) rol = "Administrador";
        else if (matricula.startsWith("20")) rol = "Alumno";
        else if (matricula.startsWith("30")) rol = "Profesor";
        else rol = "Invitado";

        try (Connection conn = ConexionBD.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO usuarios (matricula, nombre, apellidos, correo, contrasena, rol) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, matricula);
            ps.setString(2, nombre);
            ps.setString(3, apellidos);
            ps.setString(4, correo);
            ps.setString(5, contrasena); // recomendable: hashear antes en producción
            ps.setString(6, rol);

            ps.executeUpdate();
        } catch (Exception e) {
            // Si hay error en BD redirigimos con flag
            response.sendRedirect("registro.html?error=db");
            return;
        }

        // Registro OK -> redirigir a index.html con query param para mensaje
        response.sendRedirect("index.html?registro=ok");
    }
}
