document.addEventListener("DOMContentLoaded", () => {
    // 1. Obtener parámetros de la URL
    const params = new URLSearchParams(window.location.search);
    const idPedido = params.get("id");
    const origen = params.get("origen"); // Leemos si viene de 'perfil' o 'carrito'

    // 2. Lógica inteligente del botón "Regresar"
    const btnRegresar = document.querySelector(".link-regresar");

    if (btnRegresar) {
        if (origen === "perfil") {
            // Si venimos del perfil, regresamos a perfil.jsp
            btnRegresar.href = "perfil.jsp";
            btnRegresar.innerText = "← Regresar al Perfil";
        } else {
            // Caso por defecto (o si viene del carrito): regresamos a carro.html
            btnRegresar.href = "carro.html";
            btnRegresar.innerText = "← Regresar al Carrito";
        }
    }

    // 3. Validar que exista un ID de pedido
    if (!idPedido) {
        alert("No se especificó un pedido.");
        // Si no hay ID, lo mandamos al lugar seguro por defecto
        window.location.href = "carro.html";
        return;
    }

    // 4. Cargar los datos del pedido
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
            if (res.status === 404) {
                throw new Error("Pedido no encontrado");
            }
            if (!res.ok) throw new Error("Error al obtener datos");
            return res.json();
        })
        .then(data => {
            renderizar(data);
        })
        .catch(err => {
            console.error(err);
            const contenedor = document.getElementById("listaProductos");
            if(contenedor) {
                contenedor.innerHTML = `<p style="color:red; padding:20px;">Error: ${err.message}</p>`;
            }
        });
}

function renderizar(data) {
    const pedido = data.pedido;
    const detalles = data.detalles;

    // 1. Llenar cabecera y resumen (Derecha)
    document.getElementById("tituloPedido").innerText = `Pedido no. ${pedido.id_pedido}`;
    document.getElementById("txtTotal").innerText = `$ ${pedido.precio_total}`;
    document.getElementById("txtTiempo").innerText = `${pedido.tiempo_total} min`;
    document.getElementById("txtEstado").innerText = capitalizar(pedido.estado);

    // 2. Llenar lista de productos (Izquierda)
    const cont = document.getElementById("listaProductos");
    cont.innerHTML = "";

    if (!detalles || detalles.length === 0) {
        cont.innerHTML = "<p>No hay detalles para este pedido.</p>";
        return;
    }

    detalles.forEach(d => {
        const item = document.createElement("div");
        item.className = "item-detalle";
        item.innerHTML = `
            <div class="img-container">
                <img src="${d.src || 'src/principal/sin_imagen.png'}" alt="Prod">
            </div>
            <div class="nombre-prod">${d.nombre}</div>
            <div class="cant-prod">x${d.cantidad}</div>
        `;
        cont.appendChild(item);
    });
}

function capitalizar(str) {
    if (!str) return "";
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}