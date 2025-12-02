<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("correo") == null) {
        response.sendRedirect("index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <title>Perfil - Cafetería</title>
  <link rel="stylesheet" href="css/perfil.css">
</head>
<body>
<div class="container">
  <header>
        <nav class="navbar">
            <a href="principal.html" class="logo">Cafetería Universitaria</a>
            <div class="nav-icons">
                <a href="busqueda.html">
                    <img src="src/principal/lupa.png" alt="Buscar">
                </a>
                <a href="carrito.html">
                    <img src="src/principal/carrito.png" alt="Carrito">
                </a>
                <a href="perfil.jsp">
                    <img src="src/principal/usuario.png" alt="Perfil">
                </a>
            </div>
        </nav>
    </header>
</div>

  <div class="perfil-container">
    <div class="perfil-info">
      <img src="img/user.png" class="avatar" alt="avatar">
      <h2><%= session.getAttribute("nombre") %> <%= session.getAttribute("apellidos") %></h2>
      <p><strong>Correo:</strong> <%= session.getAttribute("correo") %></p>
      <p><strong>Estatus:</strong> <%= session.getAttribute("rol") %></p>

      <a class="btn-logout" href="LogoutServlet">Cerrar Sesión</a>
    </div>
  </div>
  <footer>
        <p>© 2025 Cafetería Universitaria - Todos los derechos reservados</p>
  </footer>
</body>
</html>
