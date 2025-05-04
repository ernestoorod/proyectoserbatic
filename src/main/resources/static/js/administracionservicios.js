document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formServicio');

    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Evita el envío normal
        guardarServicio();
    });

    // Captura mensajes de eliminación si vienen de URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('eliminado') === 'ok') {
        mostrarMensaje('Servicio eliminado correctamente.', 'green');
    } else if (urlParams.get('eliminado') === 'error') {
        mostrarMensaje('No se pudo eliminar el servicio.', 'red');
    }
});

function guardarServicio() {
    const form = document.getElementById('formServicio');
    const nombreError = document.getElementById('nombreError');
    const mensaje = document.getElementById('mensaje');

    // Limpiar mensajes anteriores
    nombreError.innerText = "";
    mensaje.innerText = "";
    mensaje.style.color = "";

    const nombreServicio = document.getElementById('nombreServicio').value.trim();
    if (nombreServicio === "") {
        nombreError.innerText = "El nombre del servicio es obligatorio.";
        return;
    }

    const formData = new FormData(form); // 🚀 aquí creas el FormData REAL

    fetch('/api/servicios/guardar', {
        method: 'POST',
        body: formData, // 👈 sin headers 'Content-Type', lo pone automáticamente FormData
    })
    .then(response => {
        if (response.redirected) {
            window.location.href = response.url;
            return;
        }
        return response.text().then(text => { throw new Error(text) });
    })
    .catch(error => {
        if (error.message.includes("Ya existe un servicio")) {
            nombreError.innerText = "Ese servicio ya existe.";
        } else {
            mensaje.innerText = "Error al guardar el servicio.";
            mensaje.style.color = "red";
        }
    });
}

function mostrarMensaje(texto, color) {
    const mensaje = document.getElementById('mensaje');
    mensaje.innerText = texto;
    mensaje.style.color = color;
}
