document.addEventListener("DOMContentLoaded", () => {
    // 1. Obtener el ID de la URL
    const params = new URLSearchParams(window.location.search);
    const idPedido = params.get("id");

    if (!idPedido) {
        alert("No se especificó un pedido.");
        window.location.href = "carro.html";
        return;
    }

    cargarDetalle(idPedido);
});

function cargarDetalle(id) {
    fetch(`ObtenerDetallePedidoServlet?id=${id}`)
        .then(res => {
            if (res.status === 401) {
                alert("Debes iniciar sesión.");
                window.location.href = "principal.html";
                return;
            }
            if (!res.ok) throw new Error("Error al obtener datos");
            return res.json();
        })
        .then(data => {
            renderizar(data);
        })
        .catch(err => {
            console.error(err);
            document.getElementById("listaProductos").innerHTML = "<p>Error al cargar el pedido.</p>";
        });
}

function renderizar(data) {
    const pedido = data.pedido;
    const detalles = data.detalles;

    // 1. Llenar cabecera y resumen
    document.getElementById("tituloPedido").innerText = `Pedido no. ${pedido.id_pedido}`;
    document.getElementById("txtTotal").innerText = `$ ${pedido.precio_total}`;
    document.getElementById("txtTiempo").innerText = `${pedido.tiempo_total} min`;
    document.getElementById("txtEstado").innerText = capitalizar(pedido.estado);

    // 2. Llenar lista de productos
    const cont = document.getElementById("listaProductos");
    cont.innerHTML = "";

    detalles.forEach(d => {
        const item = document.createElement("div");
        item.className = "item-detalle";
        item.innerHTML = `
            <div class="img-container">
                <img src="${d.src || 'src/principal/sin_imagen.png'}" alt="Prod">
            </div>
            <div class="nombre-prod">${d.nombre}</div>
            <div class="cant-prod">${d.cantidad}</div>
        `;
        cont.appendChild(item);
    });
}

function capitalizar(str) {
    if(!str) return "";
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}