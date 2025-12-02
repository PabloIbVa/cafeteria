let tiempoFiltro = 0;

document.addEventListener("DOMContentLoaded", cargarProductos);

function cargarProductos() {
    filtrar();
}

function filtrar() {
    let area = document.getElementById("areaSel").value;
    let precio = document.getElementById("precioMax").value;
    let buscar = document.getElementById("txtBuscar").value;

    fetch(`FiltrarProductos?area=${area}&precio=${precio}&tiempo=${tiempoFiltro}&buscar=${buscar}`)
      .then(r => r.json())
      .then(data => mostrarProductos(data));
}

function mostrarProductos(lista) {
    let cont = document.getElementById("contenedorProductos");
    cont.innerHTML = "";

    lista.forEach(p => {
        cont.innerHTML += `
        <div class="card">
        <table>
            <tr>
            <td><img src="${p.src}"></td>
            <td><h3>${p.nombre}</h3></td>
            <td><p>${p.descripcion}</p></td>
            <td><p><strong>$${p.costo}</strong></p></td>
            </tr>
        </table>
            <div class="cantidad">
                <button onclick="cambiar(${p.id}, -1)">-</button>
                <span id="cant-${p.id}">0</span>
                <button onclick="cambiar(${p.id}, 1)">+</button>
            </div>
        </div>`;
    });
}

function cambiar(id, delta) {
    let span = document.getElementById("cant-" + id);
    let val = parseInt(span.innerText) + delta;
    if (val < 0) val = 0;
    span.innerText = val;

    fetch("ActualizarCarrito", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `id=${id}&cantidad=${val}`
    });
}

function setTiempo(val) {
    tiempoFiltro = val;
    filtrar();
}

document.getElementById("txtBuscar").addEventListener("keyup", filtrar);
document.getElementById("areaSel").addEventListener("change", filtrar);
document.getElementById("precioMax").addEventListener("input", filtrar);
