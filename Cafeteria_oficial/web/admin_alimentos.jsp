<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Seguridad: Solo el administrador puede ver esta página
    if (session == null || !"Administrador".equals(session.getAttribute("rol"))) {
        response.sendRedirect("index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Administrar Alimentos</title>
    <link rel="stylesheet" href="css/admin_alimentos.css">
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
        
        <div class="search-bar-container">
            <input type="text" id="txtBuscar" placeholder="Buscar por ID, nombre, descripción...">
        </div>

        <div class="content-wrapper">
            
            <aside class="form-panel">
                <h2 id="formTitle">Nuevo Producto</h2>
                <form id="formProducto">
                    <input type="hidden" id="txtId" name="id">

                    <label>Nombre del Producto</label>
                    <input type="text" id="txtNombre" name="nombre" required>

                    <label>Precio ($)</label>
                    <input type="number" id="txtPrecio" name="precio" step="0.50" required>

                    <label>Tiempo (min)</label>
                    <input type="number" id="txtTiempo" name="tiempo" required>

                    <label>Área</label>
                    <select id="selArea" name="area" required>
                        <option value="comidas">Comidas</option>
                        <option value="bebidas">Bebidas</option>
                        <option value="snacks">Snacks</option>
                    </select>

                    <label>URL Imagen (src)</label>
                    <input type="text" id="txtSrc" name="src" placeholder="src/productos/..." required>

                    <label>Descripción</label>
                    <textarea id="txtDescripcion" name="descripcion" rows="4" required></textarea>

                    <div class="form-buttons">
                        <button type="submit" class="btn-aplicar">Aplicar</button>
                        <button type="button" class="btn-limpiar" onclick="limpiarFormulario()">Limpiar / Nuevo</button>
                    </div>
                </form>
            </aside>

            <section class="list-panel">
                <div id="listaProductos">
                    </div>
            </section>
        </div>
        
        <div class="footer-actions">
            <a href="perfil.jsp" class="link-regresar">Regresar al Perfil</a>
        </div>
    </div>

    <footer>
        <p>© 2025 Cafetería Universitaria - Todos los derechos reservados</p>
    </footer>

    <script src="js/admin_alimentos.js"></script>
</body>
</html>