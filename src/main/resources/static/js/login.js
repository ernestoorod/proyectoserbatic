document.getElementById("loginForm").addEventListener("submit", function(event) {
    const user = this.user.value.trim();
    const pass = this.pass.value.trim();

    let valid = true;

    document.getElementById("userError").textContent = "";
    document.getElementById("passError").textContent = "";

    if (!user) {
        document.getElementById("userError").textContent = "Por favor, introduce tu nombre de usuario.";
        valid = false;
    }

    if (!pass) {
        document.getElementById("passError").textContent = "Por favor, introduce tu contraseña.";
        valid = false;
    }

    if (!valid) {
        event.preventDefault();
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const campos = document.querySelectorAll("input, select");

    campos.forEach(campo => {
        if (campo.value.trim() !== "") {
            campo.classList.add("relleno");
        }

        campo.addEventListener("input", () => {
            campo.classList.toggle("relleno", campo.value.trim() !== "");
        });

        if (campo.tagName === "SELECT") {
            campo.addEventListener("change", () => {
                campo.classList.toggle("relleno", campo.value !== "");
            });
        }
    });

    const togglePassword = document.getElementById("togglePassword");
    const passwordInput = document.getElementById("passwordInput");
    const eyeIcon = document.getElementById("eyeIcon");

    if (togglePassword && passwordInput && eyeIcon) {
        togglePassword.addEventListener("click", () => {
            const isVisible = togglePassword.getAttribute("data-visible") === "true";
            const newType = isVisible ? "password" : "text";
            const newIcon = isVisible ? "/img/ojoabierto.png" : "/img/ojocerrado.png";

            passwordInput.setAttribute("type", newType);
            eyeIcon.setAttribute("src", newIcon);
            togglePassword.setAttribute("data-visible", !isVisible);
        });
    }

});
