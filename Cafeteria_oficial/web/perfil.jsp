<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Validar sesión
    if (session == null || session.getAttribute("correo") == null) {
        response.sendRedirect("index.html");
        return;
    }
    
    // Obtener el rol para usarlo en las condiciones
    String rolUsuario = (String) session.getAttribute("rol");
    boolean isAdmin = "Administrador".equals(rolUsuario);
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
            <%-- Condición 1: El Logo. Si es Admin no lleva enlace a principal --%>
            <% if (isAdmin) { %>
                <span class="logo">Panel Administrativo</span>
            <% } else { %>
                <a href="principal.html" class="logo">Cafetería Universitaria</a>
            <% } %>

            <div class="nav-icons">
                <%-- Condición 2: Iconos. Si es Admin, NO mostramos lupa ni carrito --%>
                <% if (!isAdmin) { %>
                    <a href="busqueda.html">
                        <img src="src/principal/lupa.png" alt="Buscar">
                    </a>
                    <a href="carro.html">
                        <img src="src/principal/carrito.png" alt="Carrito">
                    </a>
                <% } %>
                
                <a href="perfil.jsp">
                    <img src="src/principal/usuario.png" alt="Perfil">
                </a>
            </div>
        </nav>
    </header>
</div>

  <div class="perfil-container">
    <div class="perfil-wrapper">
        
        <div class="perfil-info">
            <h2><%= session.getAttribute("nombre") %> <%= session.getAttribute("apellidos") %></h2>
            <p><strong>Correo:</strong> <%= session.getAttribute("correo") %></p>
            <p><strong>Estatus:</strong> <%= session.getAttribute("rol") %></p>
            <a class="btn-logout" href="LogoutServlet">Cerrar Sesión</a>
        </div>

        <% if (isAdmin) { %>
            <div class="admin-panel">
                <h3>Acciones de Administrador</h3>
                <div class="admin-buttons-container">
                    <a href="admin_alimentos.jsp" class="btn-admin">
                        Administrar Alimentos
                    </a>
                    <a href="admin_pedidos.jsp" class="btn-admin">
                        Administrar Pedidos
                    </a>
                </div>
            </div>
        <% } else { %>
            <div class="historial-compras">
                <h3>Historial de compras</h3>
                <div id="contenedorPedidos"></div>
            </div>
        <% } %>

    </div> 
  </div>

  <footer>
        <p>© 2025 Cafetería Universitaria - Todos los derechos reservados</p>
  </footer>

  <%-- Solo cargamos el script de historial si NO es administrador --%>
  <% if (!isAdmin) { %>
      <script src="js/historial.js"></script>
  <% } %>
</body>
</html>