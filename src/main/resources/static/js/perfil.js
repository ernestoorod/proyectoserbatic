document.addEventListener("DOMContentLoaded", function () {
    const formulario = document.getElementById("perfilForm");

    formulario.addEventListener("submit", function (event) {
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

        // Validaciones específicas
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

        // Validar nueva contraseña solo si intenta modificarla
        if (nuevaPass !== "" || repetirPass !== "") {
            const regexContrasena = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
            if (!regexContrasena.test(nuevaPass)) {
                document.getElementById("errorNuevaPass").textContent = "Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.";
                valido = false;
            }
            if (nuevaPass !== repetirPass) {
                document.getElementById("errorRepetirPass").textContent = "Las nuevas contraseñas no coinciden.";
                valido = false;
            }
        }

        if (!valido) {
            event.preventDefault(); // 🚫 No enviar formulario si hay errores
        }
    });

    // Comportamiento visual de campos rellenados
    const campos = document.querySelectorAll("input");

    campos.forEach(campo => {
        if (campo.value.trim() !== "") {
            campo.classList.add("relleno");
        }

        campo.addEventListener("input", () => {
            if (campo.value.trim() !== "") {
                campo.classList.add("relleno");
            } else {
                campo.classList.remove("relleno");
            }
        });
    });
});

// Funciones para manejar los modales de actualización de contraseña
function mostrarModal() {
    document.getElementById('modal').style.display = 'flex';
    document.getElementById('errorMensaje').style.display = 'none';
    document.getElementById('passActual').value = "";
    document.getElementById('passActual').focus();
}

function cerrarModal() {
    document.getElementById('modal').style.display = 'none';
}

function confirmarActualizacion() {
    const passActual = document.getElementById('passActual').value.trim();
    const errorMensaje = document.getElementById('errorMensaje');

    if (passActual === "") {
        errorMensaje.textContent = "Debes escribir tu contraseña actual.";
        errorMensaje.style.display = 'block';
        return;
    }

    fetch('/perfil/verificar-pass', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ passActual: passActual })
    })
    .then(response => response.json())
    .then(data => {
        if (data.correcto) {
            document.getElementById('hiddenPassActual').value = passActual;
            cerrarModal();
            document.getElementById('perfilForm').submit();
        } else {
            errorMensaje.textContent = "Contraseña incorrecta. Intenta de nuevo.";
            errorMensaje.style.display = 'block';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        errorMensaje.textContent = "Error de conexión. Intenta más tarde.";
        errorMensaje.style.display = 'block';
    });
}

function handleKeyPress(event) {
    if (event.key === "Enter") {
        confirmarActualizacion();
    }
}

// Funciones para manejar los modales de eliminación de cuenta
function mostrarModalEliminar() {
    document.getElementById('modalEliminar').style.display = 'flex';
}

function cerrarModalEliminar() {
    document.getElementById('modalEliminar').style.display = 'none';
}

function confirmarEliminacion() {
    fetch('/perfil/eliminar', {
        method: 'POST'
    })
    .then(response => {
        if (response.redirected) {
            window.location.href = response.url;
        } else {
            alert("Error al eliminar cuenta. Intenta más tarde.");
            cerrarModalEliminar();
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("Error de conexión. Intenta más tarde.");
        cerrarModalEliminar();
    });
}
