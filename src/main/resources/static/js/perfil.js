// src/main/resources/static/js/perfil.js
document.addEventListener("DOMContentLoaded", function () {
    const formulario = document.getElementById("perfilForm");

    formulario.addEventListener("submit", function (event) {
        event.preventDefault();  // detenemos siempre para abrir luego el modal
        const nombre = this.fullName.value.trim();
        const usuario = this.user.value.trim();
        const correo = this.email.value.trim();
        const telefono = this.phone.value.trim();
        const direccion = this.address.value.trim();
        const nuevaPass = this.nuevaPass.value;
        const repetirPass = this.repetirPass.value;

        let valido = true;
        // Limpiar errores anteriores
        document.querySelectorAll(".error").forEach(e => e.textContent = "");

        if (nombre.length < 3) {
            document.getElementById("errorNombre").textContent = "El nombre debe tener al menos 3 caracteres.";
            valido = false;
        }
        if (usuario.length < 3) {
            document.getElementById("errorUsuario").textContent = "El usuario debe tener al menos 3 caracteres.";
            valido = false;
        }
        const regexCorreo = /^[\w\.-]+@[\w\.-]+\.(com|es|org)$/;
        if (!regexCorreo.test(correo)) {
            document.getElementById("errorCorreo").textContent = "Correo inválido o dominio no permitido (.com, .es, .org).";
            valido = false;
        }
        if (!/^\d{9}$/.test(telefono)) {
            document.getElementById("errorTelefono").textContent = "El teléfono debe tener exactamente 9 dígitos.";
            valido = false;
        }
        if (direccion.length < 5) {
            document.getElementById("errorDireccion").textContent = "La dirección debe tener al menos 5 caracteres.";
            valido = false;
        }
        if (nuevaPass !== "" || repetirPass !== "") {
            const regexContrasena = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
            if (!regexContrasena.test(nuevaPass)) {
                document.getElementById("errorNuevaPass").textContent =
                    "Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.";
                valido = false;
            }
            if (nuevaPass !== repetirPass) {
                document.getElementById("errorRepetirPass").textContent = "Las nuevas contraseñas no coinciden.";
                valido = false;
            }
        }

        if (!valido) return;    // hay errores: no abrimos modal
        mostrarModal();          // todo OK → mostramos modal de confirmación
    });

    // Relleno visual de inputs
    document.querySelectorAll("input").forEach(campo => {
        if (campo.value.trim() !== "") campo.classList.add("relleno");
        campo.addEventListener("input", () => {
            campo.classList.toggle("relleno", campo.value.trim() !== "");
        });
    });
});

function mostrarModal() {
    document.getElementById('modalTexto1').textContent = "Por favor, introduce tu contraseña actual.";
    const modalEl = document.getElementById('modal');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();

    document.getElementById('confirmarActualizarBtn')
        .onclick = () => confirmarActualizacion(modal);
}

function confirmarActualizacion(modalInstance) {
    const passActual = document.getElementById('passActual').value.trim();
    const mensaje = document.getElementById('modalTexto1');

    if (!passActual) {
        mensaje.textContent = "Debes escribir tu contraseña actual.";
        return;
    }

    fetch('/perfil/verificar-pass', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ passActual })
    })
    .then(res => res.json())
    .then(data => {
        if (data.correcto) {
            document.getElementById('hiddenPassActual').value = passActual;
            modalInstance.hide();
            document.getElementById('perfilForm').submit();
        } else {
            mensaje.textContent = "Contraseña incorrecta.";
        }
    })
    .catch(() => {
        mensaje.textContent = "Error de conexión. Intenta más tarde.";
    });
}

function mostrarModalEliminar() {
    const modalEl = document.getElementById('modalEliminar');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();

    document.getElementById('confirmarEliminarBtn')
        .onclick = () => confirmarEliminacion();
}

function confirmarEliminacion() {
    // Enviamos formulario oculto; el redirect del servidor se aplicará automáticamente
    document.getElementById('eliminarForm').submit();
}
