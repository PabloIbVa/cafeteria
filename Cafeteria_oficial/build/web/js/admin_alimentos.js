let productosGlobal = []; // Guardamos copia local para buscar rápido

document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();
    
    // Evento para busqueda en tiempo real
    document.getElementById("txtBuscar").addEventListener("keyup", filtrarLocalmente);

    // Evento submit del formulario
    document.getElementById("formProducto").addEventListener("submit", guardarProducto);
});

function cargarProductos() {
    fetch("AdminAlimentosServlet")
        .then(r => r.json())
        .then(data => {
            productosGlobal = data; // Guardamos en memoria
            renderizar(productosGlobal);
        })
        .catch(err => console.error("Error cargando productos:", err));
}

// Filtra sobre el array en memoria (muy rápido)
function filtrarLocalmente() {
    const texto = document.getElementById("txtBuscar").value.toLowerCase();
    
    const filtrados = productosGlobal.filter(p => {
        // Buscar por ID, Nombre, Descripción o Tiempo
        return p.id_producto.toString().includes(texto) ||
               p.nombre.toLowerCase().includes(texto) ||
               p.descripcion.toLowerCase().includes(texto) ||
               p.tiempo.toString().includes(texto);
    });
    renderizar(filtrados);
}

function renderizar(lista) {
    const cont = document.getElementById("listaProductos");
    cont.innerHTML = "";

    lista.forEach(p => {
        // Determinamos clases y textos según estado
        const esEliminado = (p.estado === 0);
        const claseCard = esEliminado ? "card eliminado" : "card";
        const textoEstado = esEliminado ? "<span class='status-text status-eliminado'>ELIMINADO</span>" : "<span class='status-text status-activo'>EXISTENTE</span>";
        
        // Botón Eliminar vs Agregar (Reactivar)
        let botonAccion = "";
        if (esEliminado) {
            botonAccion = `<button type="button" class="btn-agregar" onclick="cambiarEstado(${p.id_producto}, 1)">Agregar</button>`;
        } else {
            botonAccion = `<button type="button" class="btn-eliminar" onclick="cambiarEstado(${p.id_producto}, 0)">Eliminar</button>`;
        }

        const div = document.createElement("div");
        div.className = claseCard;
        div.innerHTML = `
            <img src="${p.src}" alt="${p.nombre}">
            <div class="card-info">
                <h3>${p.nombre} (ID: ${p.id_producto})</h3>
                <p class="card-price">$${p.costo.toFixed(2)}</p>
                <p>${p.descripcion}</p>
                <p><small>Tiempo: ${p.tiempo} min | Área: ${p.area}</small></p>
                ${textoEstado}
            </div>
            <div class="card-actions">
                <button type="button" class="btn-modificar" onclick='llenarFormulario(${JSON.stringify(p)})'>Modificar</button>
                ${botonAccion}
            </div>
        `;
        cont.appendChild(div);
    });
}

// Pone los datos en el formulario
function llenarFormulario(p) {
    document.getElementById("formTitle").innerText = "Modificar Producto #" + p.id_producto;
    document.getElementById("txtId").value = p.id_producto;
    document.getElementById("txtNombre").value = p.nombre;
    document.getElementById("txtPrecio").value = p.costo;
    document.getElementById("txtTiempo").value = p.tiempo;
    document.getElementById("selArea").value = p.area;
    document.getElementById("txtSrc").value = p.src;
    document.getElementById("txtDescripcion").value = p.descripcion;
}

// Limpia para crear uno nuevo
function limpiarFormulario() {
    document.getElementById("formTitle").innerText = "Nuevo Producto";
    document.getElementById("formProducto").reset();
    document.getElementById("txtId").value = "0"; // 0 indica nuevo
}

// Enviar Nuevo o Editar
function guardarProducto(e) {
    e.preventDefault();
    const data = new FormData(document.getElementById("formProducto"));
    
    fetch("AdminAlimentosServlet?accion=guardar", {
        method: "POST",
        body: new URLSearchParams(data) // Enviamos como form-urlencoded
    })
    .then(res => {
        if(res.ok) {
            alert("Producto guardado correctamente");
            limpiarFormulario();
            cargarProductos(); // Recargar lista
        } else {
            alert("Error al guardar");
        }
    });
}

// Eliminar (0) o Reactivar (1)
function cambiarEstado(id, nuevoEstado) {
    const accionTexto = nuevoEstado === 0 ? "eliminar" : "reactivar/agregar";
    if(!confirm(`¿Seguro que deseas ${accionTexto} este producto?`)) return;

    fetch("AdminAlimentosServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `accion=estado&id=${id}&estado=${nuevoEstado}`
    })
    .then(res => {
        if(res.ok) {
            cargarProductos();
        } else {
            alert("Error al cambiar estado");
        }
    });
}