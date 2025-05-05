document.addEventListener("DOMContentLoaded", function () {
    var map = L.map('map').setView([40.4168, -3.7038], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    fetch(`/api/empresas?servicioID=${servicioID}`)
        .then(response => response.json())
        .then(empresas => {
            const markers = [];
            empresas.forEach(empresa => {
                if (empresa.latitud && empresa.longitud) {
                    const marker = L.marker([empresa.latitud, empresa.longitud])
                        .addTo(map)
                        .bindPopup(`
                            <div style="min-width: 200px;">
                                <h3 style="margin: 0 0 5px 0;">${empresa.nombre}</h3>
                                <p style="margin: 0;"><strong>Dirección:</strong> ${empresa.direccion}</p>
                                <p style="margin: 0;"><strong>Horario:</strong> ${empresa.horaApertura} - ${empresa.horaCierre}</p>
                                <a href="/empresa?empresaID=${empresa.id}" style="display:inline-block;margin-top:8px;color:#007bff;">Ver detalles</a>
                            </div>
                        `);
                    markers.push(marker.getLatLng());
                }
            });

            if (markers.length > 0) {
                const bounds = L.latLngBounds(markers);
                map.fitBounds(bounds, {
                    padding: [50, 50],
                    maxZoom: 14
                });
                map.invalidateSize();
            }
        })
        .catch(error => {
            console.error("Error al cargar las empresas:", error);
        });
});
