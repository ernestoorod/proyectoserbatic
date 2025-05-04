let serviciosSidebar = [];
let totalServicios = 0;

function mostrarServicio(servicioSeleccionado, nombreServicio) {
  document.querySelectorAll('.servicio').forEach(s => s.style.display = 'none');
  const servicioDiv = document.getElementById(servicioSeleccionado);
  if (servicioDiv) {
    servicioDiv.style.display = 'block';
  }

  document.querySelectorAll('.sidebar li').forEach(li => li.classList.remove('active'));
  const li = serviciosSidebar.find(li => li.getAttribute('data-servicio') === servicioSeleccionado);
  if (li) {
    li.classList.add('active');
  }

  activarHover(servicioSeleccionado);

  const servicioTitulo = document.getElementById('servicio-titulo');
  const servicioEnlace = document.getElementById('servicio-enlace');
  if (servicioTitulo && servicioEnlace) {
    servicioTitulo.innerText = `Los mejores ${nombreServicio}`;
    servicioEnlace.href = `/empresas?servicioID=${encodeURIComponent(servicioSeleccionado)}`;
  }

  const indexServicioActual = serviciosSidebar.findIndex(el => el.getAttribute('data-servicio') === servicioSeleccionado);
  const pagination = document.getElementById('pagination');
  if (pagination && indexServicioActual !== -1) {
    pagination.textContent = `${indexServicioActual + 1} / ${totalServicios}`;
  }

  // Cambiar imagen de fondo
  const bgImg = document.getElementById('bg-image');
  if (bgImg) {
    const newSrc = `/api/servicios/imagen/${servicioSeleccionado}`;
    const tempImg = new Image();
    tempImg.onload = () => {
      bgImg.src = newSrc;
      bgImg.classList.add('loaded');
    };
    tempImg.onerror = () => {
      bgImg.src = '/images/default-background.jpg'; // Aquí puedes poner una imagen por defecto si quieres
      bgImg.classList.remove('loaded');
    };
    tempImg.src = newSrc;
  }
}

function activarHover(servicioId) {
  const container = document.getElementById(servicioId);
  if (container) {
    const titles = container.querySelectorAll('.title');
    titles.forEach(title => {
      title.addEventListener('mouseenter', () => {
        titles.forEach(t => {
          t.classList.remove('highlighted');
          t.classList.add('fade');
        });
        title.classList.remove('fade');
        title.classList.add('highlighted');
      });
      title.addEventListener('mouseleave', () => {
        titles.forEach(t => {
          t.classList.remove('highlighted');
          t.classList.add('fade');
        });
      });
    });
  }
}

function mostrarServicioDesdeData(element) {
  const servicioSeleccionado = element.getAttribute('data-servicio');
  const nombreServicio = element.getAttribute('data-nombre') || servicioSeleccionado;
  if (servicioSeleccionado && nombreServicio) {
    mostrarServicio(servicioSeleccionado, nombreServicio);
  }
}

function revealLinks() {
  const name = document.getElementById("userName");
  const links = document.getElementById("userLinks");
  if (name && links) {
    name.style.display = "none";
    links.style.display = "flex";
  }
}

function revealMenu() {
  const toggle = document.getElementById("menuToggle");
  const links = document.getElementById("menuLinks");
  if (toggle && links) {
    toggle.style.display = "none";
    links.style.display = "flex";
  }
}

document.addEventListener('DOMContentLoaded', () => {
  let todosLosServicios = Array.from(document.querySelectorAll('.sidebar li[data-servicio]'));

  if (!todosLosServicios || todosLosServicios.length === 0) {
    console.warn("No hay servicios para mostrar en el sidebar.");

    const sidebar = document.querySelector('.sidebar');
    if (sidebar) sidebar.style.display = 'none';
    const titulo = document.getElementById('servicio-titulo');
    const enlace = document.getElementById('servicio-enlace');
    const pagination = document.getElementById('pagination');
    if (titulo) titulo.innerText = '';
    if (enlace) enlace.style.display = 'none';
    if (pagination) pagination.textContent = '';
    return;
  }

  if (todosLosServicios.length > 4) {
    serviciosSidebar = todosLosServicios.slice(0, 4);
    totalServicios = 4;
  } else {
    serviciosSidebar = todosLosServicios;
    totalServicios = todosLosServicios.length;
  }

  todosLosServicios.forEach(servicio => servicio.style.display = 'none');
  serviciosSidebar.forEach(servicio => servicio.style.display = 'block');

  const primerServicio = serviciosSidebar[0];
  if (primerServicio) {
    mostrarServicioDesdeData(primerServicio);
  }
});

// Control para cerrar menús si haces click fuera
document.addEventListener("click", function (e) {
  const userSwitch = document.querySelector(".user-switch");
  const name = document.getElementById("userName");
  const userLinks = document.getElementById("userLinks");
  const menuWrapper = document.querySelector(".menu-wrapper");
  const menuToggle = document.getElementById("menuToggle");
  const menuLinks = document.getElementById("menuLinks");

  if (userSwitch && !userSwitch.contains(e.target)) {
    if (name) name.style.display = "inline";
    if (userLinks) userLinks.style.display = "none";
  }

  if (menuWrapper && !menuWrapper.contains(e.target)) {
    if (menuToggle) menuToggle.style.display = "inline";
    if (menuLinks) menuLinks.style.display = "none";
  }
});
