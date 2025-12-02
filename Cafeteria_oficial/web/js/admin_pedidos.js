let pedidosGlobal = [];

document.addEventListener("DOMContentLoaded", () => {
    cargarPedidos();

    // Filtro en tiempo real
    const txtBuscar = document.getElementById("txtBuscar");
    if(txtBuscar) {
        txtBuscar.addEventListener("keyup", filtrarLocalmente);
    }
});

function cargarPedidos() {
    fetch("AdminPedidosServlet")
        .then(r => r.json())
        .then(data => {
            pedidosGlobal = data;
            renderizar(pedidosGlobal);
        })
        .catch(err => console.error("Error al cargar pedidos:", err));
}

function filtrarLocalmente() {
    const texto = document.getElementById("txtBuscar").value.toLowerCase();

    const filtrados = pedidosGlobal.filter(p => {
        // Buscamos por ID, Nombre de Usuario, Descripción de productos o Estado
        return p.id_pedido.toString().includes(texto) ||
               (p.usuario && p.usuario.toLowerCase().includes(texto)) ||
               (p.descripcion && p.descripcion.toLowerCase().includes(texto)) ||
               (p.estado && p.estado.toLowerCase().includes(texto));
    });
    renderizar(filtrados);
}

function renderizar(lista) {
    const cont = document.getElementById("listaPedidos");
    if(!cont) return;
    cont.innerHTML = "";

    if(lista.length === 0) {
        cont.innerHTML = "<p style='text-align:center; padding:20px;'>No se encontraron pedidos.</p>";
        return;
    }

    lista.forEach(p => {
        const div = document.createElement("div");
        div.className = "pedido-card";
        
        // Generamos el HTML
        div.innerHTML = `
            <div class="img-box">
                <img src="${p.img || 'src/principal/sin_imagen.png'}" alt="Prod">
            </div>
            
            <div class="info-box">
                <div class="pedido-id">Pedido no. ${p.id_pedido}</div>
                <div class="pedido-usuario">Cliente: ${p.usuario}</div>
                <div class="pedido-desc">
                    ${p.descripcion || 'Sin descripción'}
                </div>
            </div>

            <div class="action-box">
                <select id="sel-${p.id_pedido}" class="estado-select">
                    <option value="pendiente" ${sel(p.estado, 'pendiente')}>Pendiente</option>
                    <option value="preparando" ${sel(p.estado, 'preparando')}>Preparando</option>
                    <option value="listo" ${sel(p.estado, 'listo')}>Listo</option>
                    <option value="entregado" ${sel(p.estado, 'entregado')}>Entregado</option>
                    <option value="cancelado" ${sel(p.estado, 'cancelado')}>Cancelado</option>
                </select>
                <button class="btn-actualizar" onclick="actualizarEstado(${p.id_pedido})">Actualizar</button>
            </div>
        `;
        cont.appendChild(div);
    });
}

// Helper para marcar la opción seleccionada
function sel(actual, opcion) {
    return (actual && actual.toLowerCase() === opcion) ? "selected" : "";
}

function actualizarEstado(idPedido) {
    const select = document.getElementById(`sel-${idPedido}`);
    const nuevoEstado = select.value;

    fetch("AdminPedidosServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `id=${idPedido}&estado=${nuevoEstado}`
    })
    .then(res => {
        if(res.ok) {
            alert("Estado actualizado correctamente.");
            cargarPedidos(); // Recargar para que se reordene la lista
        } else {
            alert("Error al actualizar.");
        }
    })
    .catch(err => console.error(err));
}