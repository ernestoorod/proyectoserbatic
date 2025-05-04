document.addEventListener("DOMContentLoaded", function() {
    const dias = ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'];
    const cont = document.getElementById('reserva-fechas');
    const hoy = new Date();

    const añoActual = hoy.getFullYear();
    const mesActual = hoy.getMonth();
    const diaActual = hoy.getDate();

    const ultimoDiaMes = new Date(añoActual, mesActual + 1, 0).getDate();
    const diasDisponibles = Math.min(8, ultimoDiaMes - diaActual + 1);

    for (let i = 0; i < diasDisponibles; i++) {
        const fechaDia = new Date(añoActual, mesActual, diaActual + i);

        const div = document.createElement('div');
        div.className = 'fecha';
        div.innerHTML = `<span>${dias[fechaDia.getDay()]}</span><strong>${fechaDia.getDate()}</strong>`;

        div.addEventListener('click', () => {
            cont.querySelectorAll('.fecha').forEach(d => d.classList.remove('selected'));
            div.classList.add('selected');

            const año = fechaDia.getFullYear();
            const mes = String(fechaDia.getMonth() + 1).padStart(2, '0');
            const diaTexto = String(fechaDia.getDate()).padStart(2, '0');
            const fechaCompleta = `${año}-${mes}-${diaTexto}`;
            localStorage.setItem('fechaSeleccionada', fechaCompleta);
        });

        cont.appendChild(div);

        if (i === 0) {
            div.classList.add('selected');

            const año = fechaDia.getFullYear();
            const mes = String(fechaDia.getMonth() + 1).padStart(2, '0');
            const diaTexto = String(fechaDia.getDate()).padStart(2, '0');
            const fechaCompleta = `${año}-${mes}-${diaTexto}`;
            localStorage.setItem('fechaSeleccionada', fechaCompleta);
        }
    }
});
