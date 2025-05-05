let sessionId = null;
    const chatTitle = document.getElementById('chatTitle');
    const chatWindow = document.getElementById('chatWindow');
    const sendBtn = document.getElementById('sendBtn');
    const questionInput = document.getElementById('question');
    const newChatIcon = document.getElementById('newChatIcon');
    const historyLink = document.getElementById('historyLink');
    const historyContainer = document.getElementById('historyContainer');
    const historyList = document.getElementById('historyList');
    const originalFaq = document.getElementById('faqContainer').cloneNode(true);

    function appendMessage(text, type) {
      const msg = document.createElement('div');
      msg.classList.add('message', type);
      msg.innerText = text;
      chatWindow.append(msg);
      chatWindow.scrollTop = chatWindow.scrollHeight;
    }

    async function createSession(firstQuestion) {
      const res = await fetch('/api/chat/session', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ firstQuestion })
      });
      sessionId = await res.json();
      await loadHistory();
    }

    async function sendToSession(text) {
      appendMessage(text, 'sent');
      const res = await fetch(`/api/chat/${sessionId}/message`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text })
      });
      const reply = await res.text();
      appendMessage(reply, 'received');
    }

    async function sendQuestion(text) {
      const faq = document.getElementById('faqContainer');
      if (faq) faq.style.display = 'none';
      if (!sessionId) {
        chatTitle.textContent = `Chat con IA - ${text}`;
        await createSession(text);
      }
      newChatIcon.style.display = 'flex';
      await sendToSession(text);
    }

    function attachFaqHandlers(container) {
      container.querySelectorAll('.faq-button').forEach(btn => btn.addEventListener('click', () => sendQuestion(btn.dataset.q)));
    }

    async function loadHistory() {
      historyList.innerHTML = '';
      const res = await fetch('/api/chat/sessions');
      const sessions = await res.json();
      sessions.sort((a, b) => a.id - b.id).forEach(s => {
        const item = document.createElement('li');
        item.className = 'history-item';
        item.innerHTML = `
          <span class="history-text">${s.id}) ${s.firstQuestion}</span>
          <button class="delete-btn" title="Eliminar">&#128465;</button>
        `;
        item.querySelector('.history-text').addEventListener('click', () => loadSession(s.id, s.firstQuestion));
        item.querySelector('.delete-btn').addEventListener('click', async (e) => {
          e.stopPropagation();
          await deleteSession(s.id);
          await loadHistory();
        });
        historyList.appendChild(item);
      });
      if (sessions.length === 0) {
        const empty = document.createElement('div'); empty.innerText = 'No hay chats en historial.'; historyList.appendChild(empty);
      }
    }

    async function deleteSession(id) {
      await fetch(`/api/chat/session/${id}`, { method: 'DELETE' });
    }

    async function loadSession(id, firstQ) {
      historyContainer.style.display = 'none';
      sessionId = id;
      chatTitle.textContent = `Chat con IA - ${firstQ}`;
      chatWindow.querySelectorAll('.message').forEach(m => m.remove());
      const faq = document.getElementById('faqContainer');
      if (faq) faq.style.display = 'none';
      const res = await fetch(`/api/chat/session/${id}/messages`);
      const messages = await res.json();
      messages.forEach(m => appendMessage(m.content, m.type.toLowerCase()));
      newChatIcon.style.display = 'flex';
    }

    // Inicialización
    attachFaqHandlers(document.getElementById('faqContainer'));
    sendBtn.addEventListener('click', () => {
      const text = questionInput.value.trim(); if (!text) return; questionInput.value = '';
      sendQuestion(text);
    });
    newChatIcon.addEventListener('click', () => {
      sessionId = null; chatTitle.textContent = 'Chat con IA'; newChatIcon.style.display = 'none';
      chatWindow.querySelectorAll('.message').forEach(m => m.remove());
      document.getElementById('faqContainer').remove();
      const freshFaq = originalFaq.cloneNode(true);
      freshFaq.id = 'faqContainer'; chatWindow.appendChild(freshFaq);
      attachFaqHandlers(freshFaq);
    });
    historyLink.addEventListener('click', async e => {
      e.preventDefault();
      const isOpen = historyContainer.style.display === 'block';
      historyContainer.style.display = isOpen ? 'none' : 'block';
      if (!isOpen) await loadHistory();
    });

    document.getElementById('backLink').addEventListener('click', function(event) {
      event.preventDefault();
      history.back();
    });

    questionInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendBtn.click();
      }
    });