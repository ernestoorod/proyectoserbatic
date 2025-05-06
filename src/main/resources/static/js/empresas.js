function handleChatClick(event, isGuest) {
    if (isGuest) {
        event.preventDefault();
        mostrarModalSesion();
        return false;
    }
    return true;
}

function mostrarModalSesion() {
    document.getElementById('modalSesionTexto').textContent = "Debes iniciar sesión para continuar.";

    const modal = new bootstrap.Modal(document.getElementById('modalSesion'));
    modal.show();
}



function cerrarModalSesion() {
    const modal = document.getElementById("modalSesion");
    modal.classList.add("hidden");
}


document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("buscador");
    const cajas = document.querySelectorAll(".caja");
    const mensaje = document.getElementById("sinResultados"); // 👈 Referencia al <p id="sinResultados">
    const userName = document.getElementById('userName');
    const userLinks = document.getElementById('userLinks');

    function normalizar(texto) {
        return texto
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    input?.addEventListener("input", function () {
        const filtro = normalizar(input.value);
        let algunaVisible = false;

        cajas.forEach(caja => {
            const nombreEmpresa = caja.querySelector(".titulo")?.innerText || "";
            const nombreNormalizado = normalizar(nombreEmpresa);

            if (nombreNormalizado.includes(filtro)) {
                caja.style.display = "block";
                algunaVisible = true;
            } else {
                caja.style.display = "none";
            }
        });

        // Mostrar/ocultar mensaje según resultados
        if (mensaje) {
            mensaje.style.display = algunaVisible ? "none" : "block";
        }
    });

    window.revealLinks = function () {
        userName.style.display = 'none';
        userLinks.style.display = 'block';
    };

    function hideLinks() {
        userLinks.style.display = 'none';
        userName.style.display = 'inline';
    }

    document.addEventListener('click', function(event) {
        const isClickInside = userLinks?.contains(event.target) || userName?.contains(event.target);
        if (!isClickInside) {
            hideLinks();
        }
    });

    window.redirigirAPrimerDia = function (empresaId) {
        fetch(`/api/reservas/empresas/${empresaId}/primer-dia-disponible`)
            .then(response => {
                if (!response.ok) throw new Error("No se pudo obtener disponibilidad.");
                return response.text();
            })
            .then(fecha => {
                window.location.href = `/empresa/${empresaId}/calendario?fecha=${fecha}`;
            })
            .catch(err => {
                alert("No hay disponibilidad en los próximos días.");
                console.error(err);
            });
    };
});
