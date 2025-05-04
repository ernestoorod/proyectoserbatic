document.addEventListener('DOMContentLoaded', function() {
  const diasDiv        = document.getElementById('dias');
  const horasDiv       = document.getElementById('horas');
  const monthLabel     = document.getElementById('monthLabel');
  const modalEl        = document.getElementById('feedbackModal');
  const modalMsg       = document.getElementById('modalMessage');
  const confirmarBtn   = document.getElementById('confirmarBtn');
  const bootstrapModal = new bootstrap.Modal(modalEl);

  let fechaSeleccionada = null;
  let horaSeleccionada  = null;
  let mesActual         = new Date();
  mesActual.setDate(1);

  const fmtDia = new Intl.DateTimeFormat('es', { weekday:'long' });
  const fmtNum = new Intl.DateTimeFormat('es', { day:'numeric' });
  const fmtMes = new Intl.DateTimeFormat('es', { month:'long', year:'numeric' });
  const fmtISO = d => {
    const y   = d.getFullYear();
    const m   = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  };

  const urlParams = new URLSearchParams(window.location.search);
  const fechaDesdeURL = urlParams.get("fecha");

  (() => {
    let isDown = false, startX, scrollLeft;
    diasDiv.addEventListener('mousedown', e => {
      isDown = true;
      diasDiv.classList.add('cursor-grabbing');
      startX = e.pageX - diasDiv.offsetLeft;
      scrollLeft = diasDiv.scrollLeft;
    });
    ['mouseleave','mouseup'].forEach(evt => {
      diasDiv.addEventListener(evt, () => {
        isDown = false;
        diasDiv.classList.remove('cursor-grabbing');
      });
    });
    diasDiv.addEventListener('mousemove', e => {
      if (!isDown) return;
      e.preventDefault();
      const x    = e.pageX - diasDiv.offsetLeft;
      const walk = (x - startX) * 2;
      diasDiv.scrollLeft = scrollLeft - walk;
    });
    diasDiv.addEventListener('touchstart', e => {
      startX = e.touches[0].pageX - diasDiv.offsetLeft;
      scrollLeft = diasDiv.scrollLeft;
    });
    diasDiv.addEventListener('touchmove', e => {
      const x    = e.touches[0].pageX - diasDiv.offsetLeft;
      const walk = (x - startX) * 2;
      diasDiv.scrollLeft = scrollLeft - walk;
    });
  })();

  function pintarMes() {
    const textoMes = fmtMes.format(mesActual);
    monthLabel.textContent = textoMes.charAt(0).toUpperCase() + textoMes.slice(1);

    diasDiv.innerHTML  = '';
    horasDiv.innerHTML = '';
    fechaSeleccionada  = null;
    horaSeleccionada   = null;
    actualizarBotonConfirmar();

    const año   = mesActual.getFullYear();
    const mes   = mesActual.getMonth();
    const total = new Date(año, mes + 1, 0).getDate();

    for (let d = 1; d <= total; d++) {
      const fecha = new Date(año, mes, d);
      const card  = document.createElement('div');
      card.className = 'card p-2 text-center flex-shrink-0';
      card.style.width = '6rem';
      card.dataset.iso = fmtISO(fecha);
      card.innerHTML = `
        <div class="fw-semibold text-capitalize small">${fmtDia.format(fecha)}</div>
        <div class="h4 fw-bold mb-0">${fmtNum.format(fecha)}</div>
      `;
      card.addEventListener('click', seleccionarDia);
      diasDiv.appendChild(card);
    }

    const fechaPrioritaria = fechaDesdeURL || localStorage.getItem('fechaSeleccionada');

    if (fechaPrioritaria) {
      const fechaObj = new Date(fechaPrioritaria);
      if (fechaObj.getMonth() === mes && fechaObj.getFullYear() === año) {
        const card = [...diasDiv.querySelectorAll('.card')].find(el => el.dataset.iso === fechaPrioritaria);
        if (card) {
          card.click();
          setTimeout(() => {
            card.scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' });
          }, 100);
          return;
        }
      }
    }

    const primero = diasDiv.querySelector('.card');
    if (primero) {
      primero.click();
      setTimeout(() => {
        primero.scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' });
      }, 100);
    }
  }

  function seleccionarDia() {
    diasDiv.querySelectorAll('.card').forEach(c => c.classList.remove('bg-secondary', 'text-white'));
    this.classList.add('bg-secondary', 'text-white');

    fechaSeleccionada = this.dataset.iso;
    horaSeleccionada  = null;
    actualizarBotonConfirmar();
    cargarHoras(fechaSeleccionada);
  }

  function cargarHoras(fechaISO) {
    const hoy = new Date();
    hoy.setHours(0,0,0,0);
    const fechaElegida = new Date(fechaISO);

    if (fechaElegida < hoy) {
      horasDiv.innerHTML = `
        <div style="font-size: 1.3rem; font-weight: 500; text-align: center; color: #777; padding: 2rem 0; width: 100%;">
          No hay horas disponibles
        </div>`;
      return;
    }

    horasDiv.innerHTML = '<div class="col-12 text-center text-muted">Cargando…</div>';

    fetch(`/api/reservas/empresas/${empresaId}/horas-libres?fecha=${fechaISO}`)
      .then(r => r.ok ? r.json() : Promise.reject())
      .then(renderHoras)
      .catch(() => mostrarModal('Error al obtener la disponibilidad'));
  }

  function renderHoras(horas) {
    horasDiv.innerHTML = '';
    if (!horas || horas.length === 0) {
      horasDiv.innerHTML = `
        <div style="font-size: 1.3rem; font-weight: 500; text-align: center; color: #777; padding: 2rem 0; width: 100%;">
          No hay horas disponibles
        </div>`;
      return;
    }
    horas.forEach(h => {
      const label = typeof h === 'string' ? h : h.label;
      const libre = typeof h === 'string' ? true : !!h.libre;
      const btn   = document.createElement('button');
      btn.textContent = label;
      btn.disabled    = !libre;
      btn.className   = libre ? 'btn btn-success w-100' : 'btn btn-light w-100 disabled';

      if (libre) {
        btn.addEventListener('click', () => {
          horasDiv.querySelectorAll('.btn').forEach(b => b.classList.remove('btn-success'));
          btn.classList.add('btn-success');
          horaSeleccionada = label;
          actualizarBotonConfirmar();
        });
      }

      const col = document.createElement('div');
      col.className = 'col';
      col.appendChild(btn);
      horasDiv.appendChild(col);
    });
  }

  function actualizarBotonConfirmar() {
    if (fechaSeleccionada && horaSeleccionada) {
      confirmarBtn.disabled = false;
      confirmarBtn.classList.remove('disabled');
      confirmarBtn.classList.replace('btn-light', 'btn-primary');
    } else {
      confirmarBtn.disabled = true;
      confirmarBtn.classList.add('disabled');
      confirmarBtn.classList.replace('btn-primary', 'btn-light');
    }
  }

  confirmarBtn.addEventListener('click', () => {
    if (fechaSeleccionada && horaSeleccionada) {
      reservar(fechaSeleccionada, horaSeleccionada);
    }
  });

  function reservar(fechaISO, horaStr) {
    fetch(`/api/reservas/empresas/${empresaId}?fecha=${fechaISO}&hora=${horaStr}`, {
      method: 'POST',
      credentials: 'include'
    })
    .then(async r => {
      if (r.ok) return;
      const msg = await r.text();
      throw new Error(msg || 'Error al reservar');
    })
    .then(() => {
      mostrarModal('¡Reserva confirmada!');
    })
    .catch(err => mostrarModal(err.message));
  }

  function mostrarModal(msg) {
    modalMsg.textContent = msg;
    bootstrapModal.show();
  }

  // document.getElementById('modalClose').addEventListener('click', () => {
  //   window.location.href = `/empresa?empresaID=${empresaId}`;
  // });

  document.getElementById('prevMonth').addEventListener('click', () => {
    mesActual.setMonth(mesActual.getMonth() - 1);
    pintarMes();
  });

  document.getElementById('nextMonth').addEventListener('click', () => {
    mesActual.setMonth(mesActual.getMonth() + 1);
    pintarMes();
  });

  if (fechaDesdeURL) {
    const f = new Date(fechaDesdeURL);
    mesActual = new Date(f.getFullYear(), f.getMonth(), 1);
  }

  pintarMes();
});
