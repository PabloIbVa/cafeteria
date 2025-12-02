document.addEventListener("DOMContentLoaded", cargarHistorial);

function cargarHistorial() {
    // Pedimos 50 pedidos para el historial del perfil
    // Nota: Llama a ListarPedidosUsuarioServlet, que es el que ya tienes funcionando
    fetch("ListarPedidosUsuarioServlet?limit=50")
        .then(res => {
            if (res.status === 401) {
                // Si la sesión expiró, redirigir al login
                window.location.href = "index.html"; 
                return;
            }
            if (!res.ok) throw new Error("Error al cargar historial");
            return res.json();
        })
        .then(data => {
            mostrarPedidos(data);
        })
        .catch(err => {
            console.error(err);
            const cont = document.getElementById("contenedorPedidos");
            if(cont) cont.innerHTML = "<p>No se pudieron cargar los pedidos.</p>";
        });
}

function mostrarPedidos(lista) {
    const cont = document.getElementById("contenedorPedidos");
    if (!cont) return; // Protección por si no existe el div
    cont.innerHTML = "";

    if (!lista || lista.length === 0) {
        cont.innerHTML = "<p>No has realizado compras aún.</p>";
        return;
    }

    lista.forEach(p => {
        // Creamos la tarjeta del pedido con la clase 'pedido-item' que definimos en el CSS
        const div = document.createElement("div");
        div.className = "pedido-item";
        
        // Generamos el HTML: Imagen, Info y Botón de Estado que lleva al detalle
        div.innerHTML = `
            <img src="${p.primer_producto_img || 'src/principal/sin_imagen.png'}" alt="Producto">
            
            <div class="pedido-info">
                <div class="pedido-titulo">
                    ${truncate(p.nombres || p.primer_producto_nombre || 'Pedido', 40)}
                </div>
                <div class="pedido-fecha">
                    ${p.fecha} &bull; <strong>$${p.precio_total}</strong>
                </div>
            </div>

            <a class="estado-btn" href="detalle_pedido.html?id=${p.id_pedido}&origen=perfil">
                <button class="estado ${estadoClass(p.estado)}">
                    ${p.estado}
                </button>
            </a>
        `;
        cont.appendChild(div);
    });
}

// --- Funciones de utilidad ---

// Cortar texto si es muy largo
function truncate(text, max) {
    if (!text) return "";
    return text.length > max ? text.substring(0, max - 3) + "..." : text;
}

// Asignar color según el estado (coincide con tu CSS)
function estadoClass(estado) {
    estado = (estado || "").toLowerCase();
    if (estado === "pendiente") return "pendiente";
    if (estado === "preparando") return "preparado";
    if (estado === "listo") return "listo";
    if (estado === "entregado") return "entregado";
    if (estado === "cancelado") return "cancelado";
    return "pendiente";
}