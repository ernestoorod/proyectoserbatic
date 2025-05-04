document.addEventListener("DOMContentLoaded", function () {
    const input = document.querySelector(".search-container input");
    const container = document.getElementById('servicios-container');
    let servicios = [];

    fetch('/api/servicios/json')
        .then(response => response.json())
        .then(data => {
            servicios = data.filter(servicio => servicio.activo);
            mostrarServiciosAleatorios(servicios);
        })
        .catch(error => {
            console.error('Error al obtener los servicios:', error);
        });

    function renderServicios(lista) {
        container.innerHTML = '';
        lista.forEach(servicio => {
            const box = document.createElement('a');
            box.className = 'service-box';
            box.href = `/empresas?servicioID=${servicio.id}`;
            box.innerHTML = `
                <img src="/api/servicios/icono/${servicio.id}" alt="${servicio.nombre}">
                <span>${servicio.nombre}</span>
            `;
            container.appendChild(box);
        });
    }

    function mostrarServiciosAleatorios(lista) {
        const copia = lista.slice();
        const aleatorios = copia.sort(() => Math.random() - 0.5).slice(0, 8);
        renderServicios(aleatorios);
    }

    input.addEventListener('input', function () {
        const termino = input.value.toLowerCase();

        if (termino === '') {
            mostrarServiciosAleatorios(servicios);
        } else {
            const filtrados = servicios.filter(servicio =>
                servicio.nombre.toLowerCase().includes(termino)
            );

            if (filtrados.length === 0) {
                container.innerHTML = '<div class="no-result">No existe servicio con ese nombre</div>';
            } else {
                renderServicios(filtrados);
            }
        }
    });
});
