<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Seguridad: Solo Admin
    if (session == null || !"Administrador".equals(session.getAttribute("rol"))) {
        response.sendRedirect("index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Administrar Pedidos</title>
    <link rel="stylesheet" href="css/admin_pedidos.css">
</head>
<body>
    
    <div class="container-nav">
        <header>
            <nav class="navbar">
                <span class="logo">Panel Administrativo</span>
                <div class="nav-icons">
                    <a href="perfil.jsp">
                        <img src="src/principal/usuario.png" alt="Perfil">
                    </a>
                </div>
            </nav>
        </header>
    </div>

    <div class="main-container">
        
        <h2 class="page-title">Pedidos</h2>

        <div class="search-bar-container">
            <input type="text" id="txtBuscar" placeholder="Buscar por No. Pedido, Usuario, Producto o Estado...">
        </div>

        <div class="list-container">
            <div id="listaPedidos">
                </div>
        </div>
        
        <div class="footer-actions">
            <a href="perfil.jsp" class="link-regresar">Regresar al Perfil</a>
        </div>
    </div>

    <footer>
        <p>© 2025 Cafetería Universitaria - Todos los derechos reservados</p>
    </footer>

    <script src="js/admin_pedidos.js"></script>
</body>
</html>