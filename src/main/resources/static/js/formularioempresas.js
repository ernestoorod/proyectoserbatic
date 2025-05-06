function actualizarPrefijo() {
    const select = document.getElementById('pais');
    const selectedOption = select.options[select.selectedIndex];
    
    if (selectedOption) {
        const prefijo = selectedOption.getAttribute('data-prefijo');
        document.getElementById('prefijo').value = prefijo || '';
    } else {
        document.getElementById('prefijo').value = '';
    }
}

let map;
let marker;

function inicializarMapa() {
    map = L.map('map').setView([40.4168, -3.7038], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
    }).addTo(map);

    map.on('click', function (e) {
        const lat = e.latlng.lat.toFixed(6);
        const lng = e.latlng.lng.toFixed(6);
        document.getElementById('latitud').value = lat;
        document.getElementById('longitud').value = lng;
        actualizarMapa(lat, lng);
        obtenerDireccion(lat, lng);
    });

    const lat = document.getElementById('latitud').value;
    const lng = document.getElementById('longitud').value;

    if (lat && lng && !isNaN(lat) && !isNaN(lng)) {
        actualizarMapa(lat, lng);
        obtenerDireccion(lat, lng);
    }
}

function actualizarMapa(lat, lng) {
    const coords = [parseFloat(lat), parseFloat(lng)];
    map.setView(coords);

    if (marker) {
        marker.setLatLng(coords);
    } else {
        marker = L.marker(coords).addTo(map);
    }
}

function obtenerDireccion(lat, lng) {
    fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`)
        .then(response => response.json())
        .then(data => {
            if (data && data.address) {
                let direccion = '';
                if (data.address.road) direccion += data.address.road;
                if (data.address.house_number) direccion += ' ' + data.address.house_number;

                const ciudad = data.address.city || data.address.town || data.address.village || '';

                document.getElementById('direccion').value = direccion.trim();
                document.getElementById('ciudad').value = ciudad;
            }
        })
        .catch(error => console.error('Error obteniendo la dirección:', error));
}

function escucharCambiosInputs() {
    const latInput = document.getElementById('latitud');
    const lngInput = document.getElementById('longitud');

    function actualizarDesdeInputs() {
        const lat = latInput.value;
        const lng = lngInput.value;

        if (!isNaN(lat) && !isNaN(lng) && lat && lng) {
            actualizarMapa(lat, lng);
            obtenerDireccion(lat, lng);
        }
    }

    latInput.addEventListener('change', actualizarDesdeInputs);
    lngInput.addEventListener('change', actualizarDesdeInputs);
}

function escucharCambioPais() {
    document.getElementById('pais').addEventListener('change', actualizarPrefijo);
}

function mostrarError(idInput, idError, mensaje) {
    const input = document.getElementById(idInput);
    const errorDiv = document.getElementById(idError);
    if (mensaje) {
        errorDiv.textContent = mensaje;
        input.classList.add('error-border');
    } else {
        errorDiv.textContent = '';
        input.classList.remove('error-border');
    }
}

function validarFormulario(event) {
    let valido = true;

    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const imagenFile = document.getElementById('imagenPortadaFile').files[0];

    // Validación nombre
    if (nombre.length < 2) {
        mostrarError('nombre', 'error-nombre', 'El nombre debe tener al menos 2 caracteres.');
        valido = false;
    } else {
        mostrarError('nombre', 'error-nombre', '');
    }

    // Validación email
    if (!/^[^@]+@[^@]+\.[a-zA-Z]{2,}$/.test(email)) {
        mostrarError('email', 'error-email', 'Ingrese un correo electrónico válido.');
        valido = false;
    } else {
        mostrarError('email', 'error-email', '');
    }

    // Validación teléfono
    if (!/^\d{6,15}$/.test(telefono)) {
        mostrarError('telefono', 'error-telefono', 'El teléfono debe tener entre 6 y 15 números.');
        valido = false;
    } else {
        mostrarError('telefono', 'error-telefono', '');
    }

    // Validación redes sociales
    const redes = [
        { campo: document.getElementById('facebook').value.trim(), id: 'facebook', dominio: /^https?:\/\/(www\.)?facebook\.com\/.+/, msg: 'URL de Facebook inválida.' },
        { campo: document.getElementById('instagram').value.trim(), id: 'instagram', dominio: /^https?:\/\/(www\.)?instagram\.com\/.+/, msg: 'URL de Instagram inválida.' },
        { campo: document.getElementById('linkedin').value.trim(), id: 'linkedin', dominio: /^https?:\/\/(www\.)?linkedin\.com\/.+/, msg: 'URL de LinkedIn inválida.' },
        { campo: document.getElementById('x').value.trim(), id: 'x', dominio: /^https?:\/\/(www\.)?x\.com\/.+/, msg: 'URL de X (Twitter) inválida.' }
    ];

    redes.forEach(red => {
        const errorId = `error-${red.id}`;
        if (red.campo && !red.dominio.test(red.campo)) {
            mostrarError(red.id, errorId, red.msg);
            valido = false;
        } else {
            mostrarError(red.id, errorId, '');
        }
    });

    // Validación imagen: obligatorio solo si NO hay imagen previa
    const hasExistingImage = !!document.querySelector('.img-preview');
    if (!imagenFile && !hasExistingImage) {
        mostrarError('imagenPortadaFile', 'error-imagen', 'Debe seleccionar una imagen de portada.');
        valido = false;
    } else {
        mostrarError('imagenPortadaFile', 'error-imagen', '');
    }

    if (!valido) {
        event.preventDefault();
    }
}

window.addEventListener('load', () => {
    actualizarPrefijo();
    escucharCambiosInputs();
    escucharCambioPais();
    inicializarMapa();

    // Vista previa de nueva imagen si el usuario elige un archivo
    document.getElementById('imagenPortadaFile').addEventListener('change', function() {
        const file = this.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = e => {
            let img = document.querySelector('.img-preview');
            if (!img) {
                img = document.createElement('img');
                img.classList.add('img-preview');
                this.parentNode.appendChild(img);
            }
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });

    document.getElementById('empresaForm').addEventListener('submit', validarFormulario);
});
