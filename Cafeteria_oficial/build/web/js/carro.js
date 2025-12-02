document.addEventListener("DOMContentLoaded", init);

function init() {
  cargarCarrito();
  cargarPedidos();
  document.getElementById("btnRealizar").addEventListener("click", realizarPedido);
}

function cargarCarrito() {
  // Asegúrate de que este nombre coincida con tu Servlet: @WebServlet("/CarritoDetalle")
  fetch("CarritoDetalle") 
    .then(res => {
      if (res.status === 401) throw new Error("No hay sesión activa");
      if (!res.ok) throw new Error("Error en petición");
      return res.json();
    })
    .then(data => {
      mostrarCarrito(data);
    })
    .catch(err => {
      console.error(err);
      document.getElementById("contenedorCarrito").innerHTML = 
        `<p style='color:red'>Debes iniciar sesión para ver el carrito.</p>`;
    });
}

function mostrarCarrito(data) {
  const cont = document.getElementById("contenedorCarrito");
  cont.innerHTML = "";
  
  // Validación extra por si data viene null
  const productos = (data && data.productos) ? data.productos : [];
  
  if (productos.length === 0) {
      cont.innerHTML = "<p>El carrito está vacío.</p>";
      document.getElementById("totalCarrito").innerText = "$0.00";
      return;
  }

  productos.forEach(p => {
    const div = document.createElement("div");
    div.className = "item";
    div.innerHTML = `
      <img src="${p.src}" alt="${p.nombre}">
      <div class="info">
        <div style="font-weight:bold">${p.nombre}</div>
        <div style="font-size:13px;color:#555">${p.descripcion || ''}</div>
        <div style="margin-top:8px" class="qty">
          <button data-id="${p.id}" class="minus">-</button>
          <span id="cant-${p.id}">${p.cantidad}</span>
          <button data-id="${p.id}" class="plus">+</button>
        </div>
      </div>
      <div style="text-align:right">
        <div>$<span id="subtotal-${p.id}">${p.subtotal.toFixed(2)}</span></div>
      </div>
    `;
    cont.appendChild(div);
  });

  document.getElementById("totalCarrito").innerText = "$" + (data.total || 0).toFixed(2);

  // listeners para +/-
  Array.from(document.querySelectorAll(".plus")).forEach(b => {
    b.addEventListener("click", () => {
      const id = b.getAttribute("data-id");
      cambiarCantidad(id, +1);
    });
  });
  Array.from(document.querySelectorAll(".minus")).forEach(b => {
    b.addEventListener("click", () => {
      const id = b.getAttribute("data-id");
      cambiarCantidad(id, -1);
    });
  });
}

function cambiarCantidad(id, delta) {
  const span = document.getElementById("cant-" + id);
  let val = parseInt(span.innerText) + delta;
  if (val < 0) val = 0;
  
  // CORRECCIÓN AQUI: Nombre del Servlet debe coincidir con @WebServlet
  fetch("ActualizarCarritoServlet", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `id=${id}&cantidad=${val}`
  }).then(res => {
    if (!res.ok) throw new Error("Error actualizando carrito");
    return res.text();
  }).then(() => {
    // recargar carrito para recomputar subtotales y total
    cargarCarrito();
  }).catch(err => console.error(err));
}

function realizarPedido() {
  if (!confirm("¿Deseas confirmar el pedido?")) return;
  
  fetch("CrearPedido", { method: "POST" })
    .then(res => {
      if (res.status === 401) throw new Error("Sesión caducada");
      if (!res.ok) throw new Error("Error creando pedido");
      return res.json();
    })
    .then(obj => {
      if (obj.ok) {
        alert("Pedido creado exitosamente. ID: " + obj.id_pedido);
        cargarCarrito();
        cargarPedidos();
      } else {
        alert("Error: " + (obj.error || "desconocido"));
      }
    })
    .catch(err => {
      console.error(err);
      alert("No se pudo crear el pedido: " + err.message);
    });
}

function cargarPedidos() {
  // Asegúrate de haber hecho Clean & Build para que este Servlet exista
  fetch("ListarPedidosUsuarioServlet")
    .then(res => {
      if (res.status === 401) throw new Error("No autorizado");
      if (!res.ok) throw new Error("Error cargando pedidos");
      return res.json();
    })
    .then(data => {
      const cont = document.getElementById("misPedidos");
      cont.innerHTML = "";
      if (!data || data.length === 0) {
          cont.innerHTML = "<p>No tienes pedidos recientes.</p>";
          return;
      }
      data.forEach(p => {
        const div = document.createElement("div");
        div.style.display = "flex";
        div.style.alignItems = "center";
        div.style.gap = "10px";
        div.style.marginBottom = "10px";
        div.innerHTML = `
          <img src="${p.primer_producto_img || 'img/product-placeholder.png'}" style="width:60px;height:60px;object-fit:cover;border-radius:6px">
          <div style="flex:1">
            <div style="font-weight:bold">${truncate(p.nombres || p.primer_producto_nombre || 'Pedido', 30)}</div>
            <div style="font-size:12px;color:#666">${p.fecha}</div>
          </div>
          <div>
            <span class="estado ${estadoClass(p.estado)}">${p.estado}</span>
          </div>
        `;
        cont.appendChild(div);
      });
    })
    .catch(err => {
      console.error(err);
      document.getElementById("misPedidos").innerHTML = "<p>Inicia sesión para ver pedidos.</p>";
    });
}

function truncate(text, max) {
  if (!text) return "";
  return text.length > max ? text.substring(0, max-3) + "..." : text;
}

function estadoClass(estado) {
  estado = (estado || "").toLowerCase();
  if (estado === "pendiente") return "pendiente";
  if (estado === "preparando") return "preparado"; // Corregí el nombre para que coincida con CSS
  if (estado === "listo") return "listo";
  if (estado === "entregado") return "entregado";
  if (estado === "cancelado") return "cancelado";
  return "pendiente";
}