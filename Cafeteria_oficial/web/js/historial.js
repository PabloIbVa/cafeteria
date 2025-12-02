// Carga y muestra los últimos 3 pedidos
document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("contenedorPedidos")) {
        cargarUltimosPedidos();
    }
});

function cargarUltimosPedidos() {
    fetch("UltimosPedidos?limit=3")
        .then(r => r.json())
        .then(data => mostrarPedidos(data))
        .catch(() => {
            const cont = document.getElementById("contenedorPedidos");
            if (cont) cont.innerHTML = "<p>No se pudieron cargar los pedidos.</p>";
        });
}

function mostrarPedidos(lista) {
    const cont = document.getElementById("contenedorPedidos");
    if (!cont) return;
    cont.innerHTML = "";

    if (!lista || lista.length === 0) {
        cont.innerHTML = "<p>No hay pedidos recientes.</p>";
        return;
    }

    lista.forEach(p => {
        const fecha = p.fecha ? new Date(p.fecha).toLocaleString("es-ES") : "—";
        const estado = p.estado ? `<span class="estado">${escapeHtml(p.estado)}</span>` : "";
        const total = typeof p.total === "number" ? `$${p.total.toFixed(2)}` : (p.total || "—");

        // items si vienen
        let itemsHTML = "";
        if (Array.isArray(p.items) && p.items.length) {
            itemsHTML = `<ul class="pedido-items">
                ${p.items.map(i =>
                    `<li class="item">
                        ${i.src ? `<img src="${escapeAttr(i.src)}" alt="img" class="mini">` : ""}
                        <span class="item-nombre">${escapeHtml(i.nombre || "")}</span>
                        <span class="item-cant">x${i.cantidad || 1}</span>
                        <span class="item-precio">$${(i.precio||0).toFixed(2)}</span>
                    </li>`
                ).join("")}
            </ul>`;
        } else {
            itemsHTML = `<p class="pedido-resumen">${escapeHtml(p.resumen || "Pedido sin detalles")}</p>`;
        }

        cont.innerHTML += `
            <div class="pedido-card">
                <div class="pedido-header">
                    <strong>Pedido #${escapeHtml(String(p.id || "—"))}</strong>
                    <small class="fecha">${fecha}</small>
                    ${estado}
                </div>
                ${itemsHTML}
                <div class="pedido-footer">
                    <span class="total">Total: <strong>${total}</strong></span>
                </div>
            </div>
        `;
    });
}

// utilidades simples para evitar inyecciones en strings
function escapeHtml(text) {
    return String(text || "").replace(/[&<>"']/g, s => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'})[s]);
}
function escapeAttr(text) {
    return escapeHtml(text).replace(/"/g, "&quot;");
}