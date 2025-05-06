const toggleBtn = document.getElementById('menu-toggle');
const sidebar = document.getElementById('sidebar');
const content = document.getElementById('main-content');
toggleBtn.addEventListener('click', () => {
  sidebar.classList.toggle('hidden');
  content.classList.toggle('full');
});

// Navigation
const navLinks = document.querySelectorAll('.nav-link');
const sections = document.querySelectorAll('.section');
navLinks.forEach(link => link.addEventListener('click', e => {
  e.preventDefault();
  navLinks.forEach(l => l.classList.remove('active'));
  link.classList.add('active');
  const target = link.dataset.section;
  sections.forEach(sec => {
    if (sec.id === target) sec.classList.add('active');
    else sec.classList.remove('active');
  });
  if (target === 'tabla') cargarUsuarios();
}));

// Carga usuarios en tabla
function cargarUsuarios() {
  fetch('http://localhost:8080/api/usuarios')
    .then(res => res.json())
    .then(data => {
      const tbody = document.getElementById('tbodyUsuarios');
      tbody.innerHTML = '';
      data.forEach(u => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>${u.id}</td>
          <td>${u.fullName}</td>
          <td>${u.user}</td>
          <td>${u.email}</td>
          <td>${u.phone}</td>
          <td>${u.address}</td>
          <td>
            <select class="form-select form-select-sm rol-select" data-id="${u.id}">
              <option value="USER" ${u.role==='USER'?'selected':''}>USER</option>
              <option value="ADMIN" ${u.role==='ADMIN'?'selected':''}>ADMIN</option>
              <option value="JEFE_EMPRESA" ${u.role==='JEFE_EMPRESA'?'selected':''}>JEFE_EMPRESA</option>
            </select>
            <button class="btn btn-sm btn-dark actualizar-rol" data-id="${u.id}">Actualizar</button>
          </td>
        `;
        tbody.appendChild(tr);
      });
      document.querySelectorAll('.actualizar-rol').forEach(btn => {
        btn.addEventListener('click', () => {
          const id = btn.dataset.id;
          const rol = document.querySelector(`select[data-id="${id}"]`).value;
          fetch(`http://localhost:8080/api/usuarios/${id}/rol`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ rol })
          })
          .then(res => {
            if (res.ok) {
              alert('Rol actualizado correctamente');
              cargarUsuarios();
            } else {
              alert('Error al actualizar el rol');
            }
          });
        });
      });
    })
    .catch(err => console.error('Error al cargar usuarios:', err));
}

// Renderiza gráfico de eventos por hora
function renderAccessChart() {
  fetch('http://localhost:8080/api/logs/hours')
    .then(res => res.json())
    .then(json => {
      const total = json.data.reduce((a,b) => a + b, 0);
      document.getElementById('total-accesses').textContent = total;
      const ctx = document.getElementById('accessChart').getContext('2d');
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: json.labels,
          datasets: [{ label: 'Eventos por Hora', data: json.data, borderWidth: 1 }]
        },
        options: { scales: { y: { beginAtZero: true } }, responsive: true }
      });
    })
    .catch(err => console.error('Error gráficos horas:', err));
}

// Renderiza doughnut de distribución de niveles
function renderLogChart() {
  fetch('http://localhost:8080/api/logs/levels')
    .then(res => res.json())
    .then(json => {
      document.getElementById('total-errors').textContent = json.ERROR || 0;
      document.getElementById('total-info').textContent = json.INFO || 0;
      document.getElementById('total-warns').textContent = json.WARN || 0;
      const ctx = document.getElementById('logChart').getContext('2d');
      new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: Object.keys(json),
          datasets: [{ data: Object.values(json), borderWidth: 2 }]
        },
        options: { plugins: { legend: { position: 'bottom' } }, responsive: true }
      });
    })
    .catch(err => console.error('Error gráficos niveles:', err));
}

// Inicialización
document.addEventListener('DOMContentLoaded', () => {
  renderAccessChart();
  renderLogChart();
});