document.addEventListener("DOMContentLoaded", function() {
    const map = L.map('map').setView([lat, lng], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { 
        attribution: '&copy; OpenStreetMap contributors' 
    }).addTo(map);

    L.marker([lat, lng]).addTo(map)
        .bindPopup(empresaNombre)
        .openPopup();
});
