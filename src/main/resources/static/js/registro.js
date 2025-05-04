document.addEventListener("DOMContentLoaded", function () {
    const formulario = document.getElementById("formularioRegistro");

    formulario.addEventListener("submit", function (event) {
        const nombre = this.nombreCompleto.value.trim();
        const usuario = this.usuario.value.trim();
        const contrasena = this.contrasena.value;
        const repetirContrasena = this.repetirContrasena.value;
        const correo = this.correo.value.trim();
        const telefono = this.telefono.value.trim();
        const direccion = this.direccion.value.trim();
        const pais = this.pais.value.trim();
        const ciudad = this.ciudad.value.trim();

        let valido = true;

        // Limpiar errores anteriores
        document.querySelectorAll(".error").forEach(e => e.textContent = "");

        // Validaciones específicas
        if (nombre === "" || nombre.split(" ").length < 3) {
            document.getElementById("errorNombre").textContent = "Debe incluir nombre y dos apellidos.";
            valido = false;
        }

        if (usuario === "" || usuario.toLowerCase() === nombre.toLowerCase().replace(/\s+/g, '')) {
            document.getElementById("errorUsuario").textContent = "El usuario no puede ser igual al nombre completo o estar vacío.";
            valido = false;
        }

        const regexContrasena = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
        if (!regexContrasena.test(contrasena)) {
            document.getElementById("errorContrasena").textContent = "Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.";
            valido = false;
        }

        if (contrasena !== repetirContrasena) {
            document.getElementById("errorRepetir").textContent = "Las contraseñas no coinciden.";
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

        if (pais.length < 2) {
            document.getElementById("errorPais").textContent = "Debe indicar un país válido.";
            valido = false;
        }

        if (ciudad.length < 2) {
            document.getElementById("errorCiudad").textContent = "Debe indicar una ciudad válida.";
            valido = false;
        }

        if (!valido) {
            event.preventDefault(); // 🚫 No se envía el formulario si hay errores
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

function togglePasswordVisibility(inputId, imgId) {
    const input = document.getElementById(inputId);
    const img = document.getElementById(imgId);
    if (!input || !img) return;

    const isVisible = input.getAttribute("type") === "text";
    input.setAttribute("type", isVisible ? "password" : "text");
    img.setAttribute("src", isVisible ? "/img/ojoabierto.png" : "/img/ojocerrado.png");
}

// Listeners para iconos
document.getElementById("togglePass1")?.addEventListener("click", () => {
    togglePasswordVisibility("contrasena", "eyeImg1");
});

document.getElementById("togglePass2")?.addEventListener("click", () => {
    togglePasswordVisibility("repetirContrasena", "eyeImg2");
});

