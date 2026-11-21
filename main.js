// ============================================================
//  SurveyCraft - Frontend Controller
// ============================================================

function getApiUrl() {
  const origin = window.location.origin;
  if (origin === 'null' || window.location.protocol === 'file:') {
    alert('ATENCIÓN: Abre la aplicación mediante el servidor local (ej. http://localhost/surveycraft-platform-version-php/).');
    return 'http://localhost/surveycraft-platform-version-php/api';
  }
  let path = window.location.pathname;
  if (path.endsWith('/index.html') || path.endsWith('/index.php')) {
    path = path.substring(0, path.lastIndexOf('/'));
  }
  path = path.replace(/\/+$/, '');
  return origin + path + '/api';
}

const API_URL = getApiUrl();

async function parseResponse(res) {
  const text = await res.text();
  try {
    return JSON.parse(text);
  } catch {
    return { message: text || `Error HTTP ${res.status}` };
  }
}

// Global State
let token       = localStorage.getItem('token');
let userRole    = localStorage.getItem('role') || 'ROLE_PLAYER';
let userName    = localStorage.getItem('username') || 'Gamer';
let isRegistering = false;
let currentSurveyId = null;
let currentEditId   = null;
let currentTab      = 'all';
let loadedSurveys   = [];

// DOM Elements
const authContainer      = document.getElementById('auth-container');
const dashboardContainer = document.getElementById('dashboard-container');
const authForm           = document.getElementById('auth-form');
const authTitle          = document.getElementById('auth-title');
const toggleAuthBtn      = document.getElementById('toggle-auth');
const registerFields     = document.getElementById('register-fields');
const submitBtn          = document.getElementById('submit-btn');
const logoutBtn          = document.getElementById('logout-btn');

// User & Role Elements
const userNameEl         = document.getElementById('user-name');
const userAvatarEl       = document.getElementById('user-avatar');
const userRoleBadgeEl    = document.getElementById('user-role-badge');
const switchRoleBtn      = document.getElementById('switch-role-btn');
const switchRoleText     = document.getElementById('switch-role-text');
const heroWelcome        = document.getElementById('hero-welcome');

// Dashboard Elements
const surveysList        = document.getElementById('surveys-list');
const surveyCount        = document.getElementById('survey-count');
const surveySearch       = document.getElementById('survey-search');
const createSurveyBtn    = document.getElementById('create-survey-btn');
const crafterActions     = document.getElementById('crafter-actions');
const surveyTabs         = document.getElementById('survey-tabs');
const tabMine            = document.getElementById('tab-mine');
const listTitle          = document.getElementById('list-title');

// Survey Modal Elements
const createSurveyModal  = document.getElementById('create-survey-modal');
const modalFormTitle     = document.getElementById('modal-form-title');
const newSurveyTitle     = document.getElementById('new-survey-title');
const newSurveyDesc      = document.getElementById('new-survey-desc');
const newSurveyGame      = document.getElementById('new-survey-game');
const newSurveyType      = document.getElementById('new-survey-type');
const questionsContainer = document.getElementById('questions-container');
const addQuestionBtn     = document.getElementById('add-question-btn');
const cancelCreateBtn    = document.getElementById('cancel-create-btn');
const saveSurveyBtn      = document.getElementById('save-survey-btn');

// Game Modal Elements
const openCreateGameBtn   = document.getElementById('open-create-game-btn');
const createGameModal     = document.getElementById('create-game-modal');
const newGameTitle        = document.getElementById('new-game-title');
const newGameDesc         = document.getElementById('new-game-desc');
const newGameCategory     = document.getElementById('new-game-category');
const cancelCreateGameBtn = document.getElementById('cancel-create-game-btn');
const saveGameBtn         = document.getElementById('save-game-btn');

// Response Modal Elements
const responseModal      = document.getElementById('response-modal');
const modalTitle         = document.getElementById('modal-title');
const modalAuthorName    = document.getElementById('modal-author-name');
const modalQuestions     = document.getElementById('modal-questions');
const closeModalBtn      = document.getElementById('close-modal-btn');
const submitResponseBtn  = document.getElementById('submit-response-btn');
const respTime           = document.getElementById('resp-time');
const respPlatform       = document.getElementById('resp-platform');

// ---- Initialization ----
function init() {
  if (token) {
    showDashboard();
  } else {
    showAuth();
  }
}

// ---- Auth View Toggle ----
toggleAuthBtn.addEventListener('click', (e) => {
  e.preventDefault();
  isRegistering = !isRegistering;
  if (isRegistering) {
    authTitle.textContent = 'Crear Nueva Cuenta';
    submitBtn.textContent = 'Registrarse';
    toggleAuthBtn.textContent = '¿Ya tienes una cuenta? Inicia sesión';
    registerFields.classList.remove('hidden');
  } else {
    authTitle.textContent = 'Bienvenido de nuevo';
    submitBtn.textContent = 'Iniciar Sesión';
    toggleAuthBtn.textContent = '¿No tienes cuenta? Regístrate aquí';
    registerFields.classList.add('hidden');
  }
});

// ---- Auth Submit ----
authForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const email    = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value.trim();

  try {
    if (isRegistering) {
      const name = document.getElementById('name').value.trim() || email.split('@')[0];
      const role = document.getElementById('role').value;

      const res = await fetch(`${API_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, username: name, role })
      });

      const data = await parseResponse(res);
      if (!res.ok) throw new Error(data.message || 'Error en el registro');

      alert('¡Registro exitoso! Por favor inicia sesión con tus credenciales.');
      isRegistering = false;
      toggleAuthBtn.click();
    } else {
      const res = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await parseResponse(res);
      if (!res.ok) throw new Error(data.message || 'Credenciales inválidas');

      token    = data.token;
      userRole = data.role;
      userName = data.username || email.split('@')[0];

      localStorage.setItem('token', token);
      localStorage.setItem('role', userRole);
      localStorage.setItem('username', userName);

      showDashboard();
    }
  } catch (err) {
    alert(err.message);
  }
});

// ---- Logout ----
logoutBtn.addEventListener('click', () => {
  token    = null;
  userRole = null;
  userName = null;
  localStorage.clear();
  showAuth();
});

function showAuth() {
  authContainer.classList.remove('hidden');
  dashboardContainer.classList.add('hidden');
}

// ---- Role Switching (Feature: Cambiar entre Crafter y Jugador) ----
switchRoleBtn.addEventListener('click', async () => {
  try {
    switchRoleBtn.disabled = true;
    switchRoleBtn.style.opacity = '0.6';

    const targetRole = (userRole === 'ROLE_CRAFTER') ? 'ROLE_PLAYER' : 'ROLE_CRAFTER';

    const res = await fetch(`${API_URL}/auth/switch-role`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ role: targetRole })
    });

    const data = await parseResponse(res);
    if (!res.ok) throw new Error(data.message || 'Error al cambiar de rol');

    token    = data.token;
    userRole = data.role;
    if (data.username) userName = data.username;

    localStorage.setItem('token', token);
    localStorage.setItem('role', userRole);

    updateUserUI();

    if (userRole === 'ROLE_CRAFTER') {
      crafterActions.classList.remove('hidden');
      if (tabMine) tabMine.classList.remove('hidden');
    } else {
      crafterActions.classList.add('hidden');
      if (tabMine) tabMine.classList.add('hidden');
      if (currentTab === 'mine') currentTab = 'all';
    }

    switchTab(currentTab || 'all');

    alert(`¡Modo cambiado! Ahora estás en ${userRole === 'ROLE_CRAFTER' ? 'Modo Crafter (Creador)' : 'Modo Jugador'}.`);
  } catch (err) {
    alert(err.message);
  } finally {
    switchRoleBtn.disabled = false;
    switchRoleBtn.style.opacity = '1';
  }
});

function updateUserUI() {
  userNameEl.textContent = userName;
  userAvatarEl.textContent = (userName.charAt(0) || 'U').toUpperCase();
  heroWelcome.innerHTML = `¡Hola de nuevo, <span class="highlight-user">${userName}</span>! 👋`;

  if (userRole === 'ROLE_CRAFTER') {
    userRoleBadgeEl.textContent = '🛠️ Crafter';
    userRoleBadgeEl.className = 'badge badge-role';
    switchRoleText.textContent = 'Cambiar a Modo Jugador';
  } else {
    userRoleBadgeEl.textContent = '🎮 Jugador';
    userRoleBadgeEl.className = 'badge badge-status';
    switchRoleText.textContent = 'Cambiar a Modo Crafter';
  }
}

async function showDashboard() {
  authContainer.classList.add('hidden');
  dashboardContainer.classList.remove('hidden');

  updateUserUI();

  if (userRole === 'ROLE_CRAFTER') {
    crafterActions.classList.remove('hidden');
    if (tabMine) tabMine.classList.remove('hidden');
  } else {
    crafterActions.classList.add('hidden');
    if (tabMine) tabMine.classList.add('hidden');
    if (currentTab === 'mine') currentTab = 'all';
  }

  switchTab(currentTab || 'all');
}

// ---- Tab Switching ----
if (surveyTabs) {
  surveyTabs.addEventListener('click', (e) => {
    const tabBtn = e.target.closest('.tab');
    if (tabBtn) {
      switchTab(tabBtn.dataset.tab);
    }
  });
}

function switchTab(tab) {
  currentTab = tab;
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  const activeBtn = document.querySelector(`.tab[data-tab="${tab}"]`);
  if (activeBtn) activeBtn.classList.add('active');

  if (tab === 'all') {
    listTitle.textContent = 'Todas las Encuestas Disponibles';
    loadSurveys();
  } else if (tab === 'answered') {
    listTitle.textContent = 'Encuestas que Has Respondido';
    loadAnsweredSurveys();
  } else if (tab === 'mine') {
    listTitle.textContent = 'Mis Encuestas Creadas';
    loadMySurveys();
  }
}

// ---- Real-time Search Filter ----
surveySearch.addEventListener('input', (e) => {
  const query = e.target.value.toLowerCase().trim();
  if (!query) {
    renderSurveys(loadedSurveys, currentTab === 'mine');
    return;
  }
  const filtered = loadedSurveys.filter(s => {
    const title   = (s.titulo || '').toLowerCase();
    const desc    = (s.descripcion || '').toLowerCase();
    const game    = (s.juegoTitulo || '').toLowerCase();
    const author  = (s.crafterUsername || '').toLowerCase();
    const cat     = (s.nombreCategoria || '').toLowerCase();
    return title.includes(query) || desc.includes(query) || game.includes(query) || author.includes(query) || cat.includes(query);
  });
  renderSurveys(filtered, currentTab === 'mine');
});

// ---- Load All Surveys ----
async function loadSurveys() {
  surveysList.innerHTML = '<div class="spinner"></div>';
  try {
    const res = await fetch(`${API_URL}/surveys`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await parseResponse(res);
    if (!res.ok) throw new Error(data.message || 'Error cargando encuestas');
    loadedSurveys = Array.isArray(data) ? data : [];
    renderSurveys(loadedSurveys, false);
  } catch (err) {
    surveysList.innerHTML = `<p class="text-muted text-center" style="grid-column: 1/-1;">${err.message || 'Error al cargar encuestas.'}</p>`;
  }
}

// ---- Load Answered Surveys (Feature: Encuestas Respondidas) ----
async function loadAnsweredSurveys() {
  surveysList.innerHTML = '<div class="spinner"></div>';
  try {
    const res = await fetch(`${API_URL}/surveys/answered`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await parseResponse(res);
    if (!res.ok) throw new Error(data.message || 'Error cargando encuestas respondidas');
    loadedSurveys = Array.isArray(data) ? data : [];
    renderSurveys(loadedSurveys, false);
  } catch (err) {
    surveysList.innerHTML = `<p class="text-muted text-center" style="grid-column: 1/-1;">${err.message || 'Error al cargar encuestas respondidas.'}</p>`;
  }
}

// ---- Load My Surveys ----
async function loadMySurveys() {
  surveysList.innerHTML = '<div class="spinner"></div>';
  try {
    const res = await fetch(`${API_URL}/surveys/my-surveys`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await parseResponse(res);
    if (!res.ok) throw new Error(data.message || 'Error cargando mis encuestas');
    loadedSurveys = Array.isArray(data) ? data : [];
    renderSurveys(loadedSurveys, true);
  } catch (err) {
    surveysList.innerHTML = `<p class="text-muted text-center" style="grid-column: 1/-1;">${err.message || 'Error al cargar mis encuestas.'}</p>`;
  }
}

// ---- Render Surveys with Author and Game Information ----
function renderSurveys(surveys, isMine) {
  surveyCount.textContent = `${surveys.length} ${surveys.length === 1 ? 'encuesta' : 'encuestas'}`;

  if (!surveys || surveys.length === 0) {
    surveysList.innerHTML = `
      <div class="glass-card text-center" style="grid-column: 1/-1; padding: 3rem 1.5rem;">
        <span style="font-size: 2.5rem; display: block; margin-bottom: 0.5rem;">🎮</span>
        <h4>No se encontraron encuestas</h4>
        <p class="text-muted">${isMine ? 'Aún no has creado encuestas. Haz clic en "+ Nueva Encuesta" para comenzar.' : 'No hay encuestas activas en este momento.'}</p>
      </div>
    `;
    return;
  }

  surveysList.innerHTML = surveys.map(s => {
    const gameName = s.juegoTitulo || (s.juegoId ? `Juego #${s.juegoId}` : 'Videojuego General');
    const catName  = s.nombreCategoria ? `🏷️ ${s.nombreCategoria}` : null;
    const author   = s.crafterUsername || 'Creador';
    const initial  = author.charAt(0).toUpperCase();
    const answeredBadge = s.yaRespondida ? `<span class="badge badge-answered">✅ Ya respondida</span>` : '';

    return `
      <div class="survey-card">
        <div>
          <div class="survey-badges">
            <span class="badge badge-game">🎮 ${gameName}</span>
            ${catName ? `<span class="badge badge-cat">${catName}</span>` : ''}
            <span class="badge badge-type">${formatSurveyType(s.tipoEncuesta)}</span>
            <span class="badge badge-status"><span class="pulse-dot"></span> ${s.estado || 'Activa'}</span>
            ${answeredBadge}
          </div>

          <h3 class="survey-title">${escapeHtml(s.titulo || 'Sin título')}</h3>
          <p class="survey-desc">${escapeHtml(s.descripcion || 'Sin descripción disponible.')}</p>

          <!-- Autor de la encuesta -->
          <div class="survey-author-bar">
            <span class="author-avatar-small">${initial}</span>
            <span>Creado por: <strong class="author-name">${escapeHtml(author)}</strong></span>
          </div>
        </div>

        <div class="survey-card-footer">
          <span class="response-counter">
            💬 <strong>${s.totalRespuestas || 0}</strong> ${s.totalRespuestas === 1 ? 'respuesta' : 'respuestas'}
          </span>
          <div style="display: flex; gap: 0.45rem;">
            ${isMine ? `
              <button onclick="window.editSurvey(${s.surveyId})" class="btn btn-secondary btn-sm">✏️ Editar</button>
              <button onclick="window.deleteSurvey(${s.surveyId})" class="btn btn-outline-danger btn-sm">🗑️</button>
            ` : s.yaRespondida ? `
              <button onclick="window.openResponseModal(${s.surveyId})" class="btn btn-secondary btn-sm" style="border-color: rgba(16, 185, 129, 0.45); color: #a7f3d0;">
                <span>📊 Ver Resultados</span>
              </button>
            ` : `
              <button onclick="window.openResponseModal(${s.surveyId})" class="btn btn-primary btn-sm">
                <span>Votar / Responder</span> →
              </button>
            `}
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function formatSurveyType(type) {
  const map = {
    'SATISFACCION': 'Satisfacción',
    'BUG_REPORT': 'Bugs',
    'FEATURE_REQUEST': 'Sugerencia',
    'BALANCE': 'Balance',
    'OTRO': 'General'
  };
  return map[type] || type || 'General';
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// ---- Load Games for dropdown ----
async function loadCategories() {
  try {
    const res = await fetch(`${API_URL}/games`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      const games = await parseResponse(res);
      if (!Array.isArray(games) || games.length === 0) {
        newSurveyGame.innerHTML = '<option value="">No hay videojuegos aún (haz clic en + para agregar uno)</option>';
      } else {
        newSurveyGame.innerHTML = '<option value="">-- Selecciona un videojuego --</option>' + games.map(g => {
          const cat = g.nombreCategoria || g.nombre_categoria || 'General';
          return `<option value="${g.id}">${g.titulo} (${cat})</option>`;
        }).join('');
      }
    }
  } catch (err) {
    console.error('Error loading games', err);
    newSurveyGame.innerHTML = '<option value="">Error cargando juegos</option>';
  }
}

// ---- Open Create Survey Modal ----
createSurveyBtn.addEventListener('click', () => {
  currentEditId = null;
  modalFormTitle.textContent = 'Crear Nueva Encuesta';
  saveSurveyBtn.textContent  = 'Guardar Encuesta';
  newSurveyTitle.value = '';
  newSurveyDesc.value  = '';
  questionsContainer.innerHTML = '';
  addQuestion(1);
  loadCategories();
  createSurveyModal.classList.remove('hidden');
});

// ---- Edit Survey ----
window.editSurvey = async (id) => {
  currentEditId = id;
  modalFormTitle.textContent = 'Editar Encuesta';
  saveSurveyBtn.textContent  = 'Actualizar Encuesta';

  try {
    const res = await fetch(`${API_URL}/surveys/${id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const survey = await parseResponse(res);
    if (!res.ok) throw new Error(survey.message || 'Error cargando encuesta');

    newSurveyTitle.value = survey.titulo;
    newSurveyDesc.value  = survey.descripcion || '';
    newSurveyType.value  = survey.tipoEncuesta || 'SATISFACCION';

    await loadCategories();
    if (survey.juegoId) newSurveyGame.value = String(survey.juegoId);

    questionsContainer.innerHTML = '';
    if (survey.preguntas && survey.preguntas.length > 0) {
      survey.preguntas.forEach((q, idx) => addQuestion(idx + 1, q));
    } else {
      addQuestion(1);
    }

    createSurveyModal.classList.remove('hidden');
  } catch (err) {
    alert(err.message);
  }
};

// ---- Delete Survey ----
window.deleteSurvey = async (id) => {
  if (!confirm('¿Estás seguro de que deseas eliminar esta encuesta?')) return;
  try {
    const res = await fetch(`${API_URL}/surveys/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) {
      const data = await parseResponse(res);
      throw new Error(data.message || 'Error al eliminar');
    }
    loadMySurveys();
  } catch (err) {
    alert(err.message);
  }
};

// ---- Close Create Modal ----
cancelCreateBtn.addEventListener('click', () => {
  createSurveyModal.classList.add('hidden');
});

// ============================================================
//  DYNAMIC QUESTION & OPTIONS BUILDER (Min 2, Sin límite máx)
// ============================================================
function addQuestion(index, data = null) {
  const qDiv = document.createElement('div');
  qDiv.className = 'question-item';

  const qText = data ? data.textoPregunta : '';
  const initialOptions = data && data.opciones && data.opciones.length >= 2
    ? data.opciones.map(o => o.textoOpcion)
    : ['', '']; // Mínimo 2 opciones por defecto

  qDiv.innerHTML = `
    <div class="question-header">
      <span class="question-tag">Pregunta #${index}</span>
      <button type="button" class="btn btn-outline-danger btn-sm remove-q-btn">✕ Eliminar Pregunta</button>
    </div>
    <div class="form-group" style="margin-bottom: 0.75rem;">
      <input type="text" class="q-text" placeholder="Ej: ¿Qué calificación le das a las mecánicas de combate?" value="${escapeHtml(qText)}">
    </div>
    <div>
      <label style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.4rem;">Opciones de respuesta (mínimo 2):</label>
      <div class="options-container"></div>
      <button type="button" class="btn-add-opt">+ Agregar otra opción</button>
    </div>
  `;

  const optionsContainer = qDiv.querySelector('.options-container');
  const addOptBtn        = qDiv.querySelector('.btn-add-opt');

  function renderOptionRow(value = '') {
    const row = document.createElement('div');
    row.className = 'option-row';
    const optLetter = String.fromCharCode(65 + optionsContainer.children.length); // A, B, C, D...

    row.innerHTML = `
      <span class="option-badge">${optLetter}</span>
      <input type="text" class="opt-input" placeholder="Escribe una opción..." value="${escapeHtml(value)}">
      <button type="button" class="btn-remove-opt" title="Eliminar opción">✕</button>
    `;

    row.querySelector('.btn-remove-opt').addEventListener('click', () => {
      if (optionsContainer.children.length > 2) {
        row.remove();
        updateOptionBadges();
      }
    });

    optionsContainer.appendChild(row);
    updateOptionBadges();
  }

  function updateOptionBadges() {
    const rows = optionsContainer.querySelectorAll('.option-row');
    rows.forEach((r, idx) => {
      r.querySelector('.option-badge').textContent = String.fromCharCode(65 + idx);
      const removeBtn = r.querySelector('.btn-remove-opt');
      if (rows.length <= 2) {
        removeBtn.disabled = true;
        removeBtn.title = 'Se requieren al menos 2 opciones';
      } else {
        removeBtn.disabled = false;
        removeBtn.title = 'Eliminar esta opción';
      }
    });
  }

  // Cargar opciones iniciales
  initialOptions.forEach(opt => renderOptionRow(opt));

  // Botón para agregar más opciones dinámicamente
  addOptBtn.addEventListener('click', () => {
    renderOptionRow('');
  });

  // Eliminar pregunta completa
  qDiv.querySelector('.remove-q-btn').addEventListener('click', () => {
    if (questionsContainer.children.length > 1) {
      qDiv.remove();
      // Renumerar preguntas restantes
      questionsContainer.querySelectorAll('.question-item').forEach((item, idx) => {
        item.querySelector('.question-tag').textContent = `Pregunta #${idx + 1}`;
      });
    } else {
      alert('La encuesta debe contener al menos una pregunta.');
    }
  });

  questionsContainer.appendChild(qDiv);
}

addQuestionBtn.addEventListener('click', () => {
  addQuestion(questionsContainer.children.length + 1);
});

// ---- Save Survey ----
saveSurveyBtn.addEventListener('click', async () => {
  const title  = newSurveyTitle.value.trim();
  const desc   = newSurveyDesc.value.trim();
  const gameId = newSurveyGame.value;
  const type   = newSurveyType.value;

  if (!title) {
    alert('Por favor ingresa un título para la encuesta.');
    return;
  }
  if (!gameId) {
    alert('Por favor selecciona o crea un videojuego para la encuesta.');
    return;
  }

  const questions = [];
  const questionItems = questionsContainer.querySelectorAll('.question-item');

  if (questionItems.length === 0) {
    alert('Debes agregar al menos una pregunta.');
    return;
  }

  for (let i = 0; i < questionItems.length; i++) {
    const item = questionItems[i];
    const text = item.querySelector('.q-text').value.trim();
    if (!text) {
      alert(`Por favor escribe el texto de la Pregunta #${i + 1}`);
      return;
    }

    const opts = [];
    item.querySelectorAll('.opt-input').forEach(input => {
      const val = input.value.trim();
      if (val) opts.push(val);
    });

    if (opts.length < 2) {
      alert(`La Pregunta #${i + 1} debe tener al menos 2 opciones de respuesta válidas.`);
      return;
    }

    questions.push({
      texto: text,
      tipoPregunta: 'SINGLE_CHOICE',
      opciones: opts
    });
  }

  const payload = {
    titulo: title,
    descripcion: desc,
    juegoId: parseInt(gameId),
    tipoEncuesta: type,
    preguntas: questions
  };

  try {
    saveSurveyBtn.disabled = true;
    const url    = currentEditId ? `${API_URL}/surveys/${currentEditId}` : `${API_URL}/surveys`;
    const method = currentEditId ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    const data = await parseResponse(res);
    if (!res.ok) throw new Error(data.message || 'Error al guardar la encuesta');

    alert(currentEditId ? '¡Encuesta actualizada con éxito!' : '¡Encuesta creada con éxito!');
    createSurveyModal.classList.add('hidden');
    if (currentTab === 'mine') loadMySurveys(); else loadSurveys();
  } catch (err) {
    alert(err.message);
  } finally {
    saveSurveyBtn.disabled = false;
  }
});

// ============================================================
//  RESPONSE MODAL
// ============================================================
closeModalBtn.addEventListener('click', () => {
  responseModal.classList.add('hidden');
});

window.openResponseModal = async (surveyId) => {
  currentSurveyId = surveyId;
  responseModal.classList.remove('hidden');
  modalQuestions.innerHTML = '<div class="spinner"></div>';

  try {
    const res = await fetch(`${API_URL}/surveys/${surveyId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const survey = await parseResponse(res);
    if (!res.ok) throw new Error(survey.message || 'Error cargando encuesta');

    modalTitle.textContent = survey.titulo;
    modalAuthorName.textContent = survey.crafterUsername || 'Creador de la encuesta';

    if (!survey.preguntas || survey.preguntas.length === 0) {
      modalQuestions.innerHTML = '<p class="text-muted text-center">Esta encuesta no contiene preguntas.</p>';
      return;
    }

    let bannerHtml = '';
    if (survey.yaRespondida) {
      bannerHtml = `
        <div class="already-answered-banner">
          <span>✅ <strong>Ya has respondido esta encuesta.</strong> A continuación puedes ver los resultados generales y tu opción votada destacada.</span>
        </div>
      `;
      submitResponseBtn.classList.add('hidden');
      if (respTime) respTime.disabled = true;
      if (respPlatform) respPlatform.disabled = true;
    } else {
      submitResponseBtn.classList.remove('hidden');
      if (respTime) respTime.disabled = false;
      if (respPlatform) respPlatform.disabled = false;
    }

    modalQuestions.innerHTML = bannerHtml + survey.preguntas.map((q, idx) => `
      <div class="response-q-card question-block">
        <p class="response-q-title"><strong>${idx + 1}.</strong> ${escapeHtml(q.textoPregunta)}</p>
        <div class="response-options-grid">
          ${q.opciones.map(opt => {
            const isMyVote = opt.miVoto === true;
            const checkedAttr = isMyVote ? 'checked' : '';
            const disabledAttr = survey.yaRespondida ? 'disabled' : '';
            const myVoteClass = isMyVote ? 'my-vote' : '';
            const myVoteLabel = isMyVote ? '<strong style="color: #a5b4fc; margin-left: 0.35rem;">(Tu voto)</strong>' : '';
            const statsBar = survey.yaRespondida ? `
              <div class="vote-percentage-bar">
                <div class="vote-percentage-fill" style="width: ${opt.porcentaje}%"></div>
              </div>
              <span class="text-hint" style="font-size: 0.75rem; margin-top: 0.2rem; display: block;">
                ${opt.votos} ${opt.votos === 1 ? 'voto' : 'votos'} (${opt.porcentaje}%)
              </span>
            ` : '';

            return `
              <label class="response-option-label ${myVoteClass}" style="${survey.yaRespondida ? 'cursor: default;' : ''}">
                <div style="width: 100%;">
                  <div style="display: flex; align-items: center; gap: 0.5rem;">
                    <input type="radio" name="q-${q.questionId}" value="${opt.optionId}" ${checkedAttr} ${disabledAttr}>
                    <span>${escapeHtml(opt.textoOpcion)} ${myVoteLabel}</span>
                  </div>
                  ${statsBar}
                </div>
              </label>
            `;
          }).join('')}
        </div>
      </div>
    `).join('');
  } catch (err) {
    modalQuestions.innerHTML = `<p class="text-muted text-center">${err.message || 'Error cargando detalles.'}</p>`;
  }
};

// ---- Submit Response ----
submitResponseBtn.addEventListener('click', async () => {
  if (!currentSurveyId) return;

  const answers = [];
  const blocks = modalQuestions.querySelectorAll('.question-block');

  for (const block of blocks) {
    const radio = block.querySelector('input[type="radio"]:checked');
    if (radio) {
      answers.push({
        questionId: parseInt(radio.name.replace('q-', '')),
        optionId:   parseInt(radio.value)
      });
    }
  }

  if (answers.length === 0) {
    alert('Por favor selecciona al menos una respuesta.');
    return;
  }

  try {
    submitResponseBtn.disabled = true;
    const res = await fetch(`${API_URL}/surveys/${currentSurveyId}/respond`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        tiempoJugadoJuego: respTime.value,
        plataformaUsada:   respPlatform.value,
        respuestas:        answers
      })
    });

    const data = await parseResponse(res);
    if (!res.ok) throw new Error(data.message || 'Error enviando respuesta');

    alert('¡Tu respuesta se registró con éxito! Gracias por participar.');
    responseModal.classList.add('hidden');
    if (currentTab === 'answered') loadAnsweredSurveys();
    else if (currentTab === 'mine') loadMySurveys();
    else loadSurveys();
  } catch (err) {
    alert(err.message);
  } finally {
    submitResponseBtn.disabled = false;
  }
});

// ============================================================
//  CREATE GAME MODAL
// ============================================================
if (openCreateGameBtn) {
  openCreateGameBtn.addEventListener('click', (e) => {
    e.preventDefault();
    newGameTitle.value    = '';
    newGameDesc.value     = '';
    newGameCategory.value = '';
    createGameModal.classList.remove('hidden');
  });
}

if (cancelCreateGameBtn) {
  cancelCreateGameBtn.addEventListener('click', () => {
    createGameModal.classList.add('hidden');
  });
}

if (saveGameBtn) {
  saveGameBtn.addEventListener('click', async () => {
    const title    = newGameTitle.value.trim();
    const desc     = newGameDesc.value.trim();
    const category = newGameCategory.value.trim();

    if (!title || !category) {
      alert('El Título y la Categoría son obligatorios.');
      return;
    }

    try {
      saveGameBtn.disabled = true;
      const res = await fetch(`${API_URL}/games`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ titulo: title, descripcion: desc, nombreCategoria: category })
      });

      const newGame = await parseResponse(res);
      if (!res.ok) throw new Error(newGame.message || 'Error al crear el videojuego');

      const catName = newGame.nombreCategoria || newGame.nombre_categoria || category;
      alert(`¡Videojuego "${newGame.titulo}" (${catName}) creado exitosamente!`);
      createGameModal.classList.add('hidden');

      await loadCategories();
      newSurveyGame.value = String(newGame.id);
    } catch (err) {
      alert(err.message);
    } finally {
      saveGameBtn.disabled = false;
    }
  });
}

// Start App
init();
