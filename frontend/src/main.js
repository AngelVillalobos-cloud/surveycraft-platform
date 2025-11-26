import './style.css';

const API_URL = 'http://localhost:8080/api';
let currentUser = null;
let newSurveyQuestions = []; 

// ==========================================
// 1. RENDERIZADO PRINCIPAL
// ==========================================
function renderApp() {
  const app = document.getElementById('app');
  
  // Estilos Inyectados (CSS)
  const customStyles = `
    <style>
        /* Estilos para Inputs */
        .input-glass {
            width: 100%; padding: 15px; background: rgba(0, 0, 0, 0.3); border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px; color: white; font-size: 1rem; transition: all 0.3s ease; outline: none;
        }
        .input-glass:hover {
            background: rgba(255, 255, 255, 0.1); border-color: rgba(255, 255, 255, 0.5);
            transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .input-glass:focus {
            border-color: #00b894; background: rgba(0, 0, 0, 0.5); box-shadow: 0 0 20px rgba(0, 184, 148, 0.2);
        }
        /* Etiquetas */
        .label-hd {
            display: block; margin-bottom: 10px; color: #ffffff; font-weight: 800; font-size: 1.1rem;
            letter-spacing: 0.5px; text-shadow: 0 2px 4px rgba(0,0,0,0.6);
        }
        /* Filas del Dashboard */
        .survey-row {
            background: linear-gradient(90deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
            backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.1); transition: all 0.3s ease;
        }
        .survey-row:hover {
            background: rgba(255,255,255,0.15); transform: translateX(5px);
            border-color: rgba(255,255,255,0.3); box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }
        /* CLASE FORZADA PARA LA TARJETA ANCHA */
        .wide-card {
            width: 600px !important; 
            max-width: 90vw !important;
            min-width: 350px;
        }
    </style>
  `;

  app.innerHTML = customStyles + `
    <section id="auth-section" style="min-height: 100vh; width: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center;">
      
      <div id="login-container" class="auth-container" style="width: auto; display: flex; justify-content: center;">
        <div class="auth-card wide-card" style="padding: 3rem; background: rgba(40, 40, 60, 0.85); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.1); box-shadow: 0 20px 60px rgba(0,0,0,0.5); border-radius: 24px;">
            <div style="text-align:center; margin-bottom: 40px;">
                <h1 style="color:white; font-size: 3.5rem; margin: 0; font-weight: 800; letter-spacing: -1px; text-shadow: 0 0 20px rgba(108, 92, 231, 0.6);">SurveyCraft</h1>
                <p class="subtitle" style="color: #a0a0ff; margin-top: 10px; font-size: 1.2rem; font-weight: 500;">Plataforma de Encuestas Gaming</p>
            </div>
          <form id="login-form">
            <div class="form-group" style="margin-bottom: 25px;"><label class="label-hd">📧 Email</label><input type="email" id="login-email" required class="input-glass"></div>
            <div class="form-group" style="margin-bottom: 30px;"><label class="label-hd">🔒 Contraseña</label><input type="password" id="login-password" required class="input-glass"></div>
            <button type="submit" class="btn btn-primary btn-block" style="background: linear-gradient(45deg, #6c5ce7, #a29bfe); border:none; font-weight:bold; padding: 18px; width: 100%; border-radius: 12px; cursor: pointer; font-size: 1.2rem; box-shadow: 0 4px 15px rgba(108, 92, 231, 0.4); transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.02)'" onmouseout="this.style.transform='scale(1)'">Iniciar Sesión</button>
          </form>
          <p class="auth-link" style="text-align: center; margin-top: 30px; color: #ccc; font-size: 1rem;">¿No tienes cuenta? <a href="#" id="show-register-link" style="color: #a29bfe; font-weight: bold; text-decoration: none; border-bottom: 1px dashed #a29bfe;">Regístrate aquí</a></p>
        </div>
      </div>

      <div id="register-container" class="auth-container hidden" style="width: auto; display: flex; justify-content: center;">
        <div class="auth-card wide-card" style="padding: 3rem; background: rgba(40, 40, 60, 0.85); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.1); border-radius: 24px; box-shadow: 0 20px 60px rgba(0,0,0,0.5);">
          <h1 style="color:white; text-align: center; margin-bottom: 40px; font-size: 2.5rem; font-weight: 800;">Crear Cuenta</h1>
          <form id="register-form">
            <div class="form-group" style="margin-bottom: 20px;"><label class="label-hd">👤 Nombre</label><input type="text" id="register-name" required class="input-glass"></div>
            <div class="form-group" style="margin-bottom: 20px;"><label class="label-hd">📧 Email</label><input type="email" id="register-email" required class="input-glass"></div>
            <div class="form-group" style="margin-bottom: 20px;"><label class="label-hd">🔒 Contraseña</label><input type="password" id="register-password" required class="input-glass"></div>
            <div class="form-group" style="margin-bottom: 30px;">
                <label class="label-hd">🎭 Rol</label>
                <select id="register-role" required class="input-glass">
                    <option value="PLAYER" style="color:black;">🎮 Jugador</option>
                    <option value="CRAFTER" style="color:black;">🔨 Creador</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary btn-block" style="background: linear-gradient(45deg, #00b894, #55efc4); border:none; font-weight:bold; color: #2d3436; padding: 18px; width: 100%; border-radius: 12px; cursor: pointer; font-size: 1.2rem; transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.02)'" onmouseout="this.style.transform='scale(1)'">Registrarse</button>
          </form>
          <p class="auth-link" style="text-align: center; margin-top: 30px; color: #ccc; font-size: 1rem;">¿Ya tienes cuenta? <a href="#" id="show-login-link" style="color: #55efc4; font-weight: bold; text-decoration: none; border-bottom: 1px dashed #55efc4;">Inicia Sesión</a></p>
        </div>
      </div>
    </section>

    <section id="dashboard-section" class="hidden">
      <nav class="navbar" style="background: rgba(0, 0, 0, 0.2); backdrop-filter: blur(10px); padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid rgba(255,255,255,0.1); position: sticky; top: 0; z-index: 100;">
        <div class="navbar-brand"><h2 style="margin:0; font-weight: 800; font-size: 1.5rem; color: white; letter-spacing: 1px;">SurveyCraft</h2></div>
        <div class="navbar-user" style="display: flex; gap: 20px; align-items: center;">
          <button id="create-btn" onclick="window.showCreateScreen()" class="btn" style="background: linear-gradient(45deg, #6c5ce7, #a29bfe); color: white; padding: 8px 20px; border-radius: 20px; font-weight: bold; border: none; box-shadow: 0 4px 15px rgba(108, 92, 231, 0.3); transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.05)'" onmouseout="this.style.transform='scale(1)'">➕ Nueva Encuesta</button>
          <span style="font-weight: 500; color: #ddd;">Hola, <span id="user-name" style="font-weight: bold; color: white;">Usuario</span></span>
          <button id="logout-btn" class="btn" style="background: rgba(255, 71, 87, 0.2); color: #ff4757; padding: 5px 15px; font-size: 0.9rem; border: 1px solid #ff4757; border-radius: 20px; cursor: pointer;">Salir</button>
        </div>
      </nav>

      <main class="container" style="max-width: 1200px; margin: 0 auto; padding: 3rem 2rem;">
        <h2 style="font-size: 2rem; margin-bottom: 2rem; font-weight: 700; color: white; text-shadow: 0 2px 10px rgba(0,0,0,0.3);">Encuestas Disponibles</h2>
        <div id="surveys-container" class="surveys-grid" style="display: flex; flex-direction: column; gap: 20px;">
          <p class="loading" style="color:white;">Cargando encuestas...</p>
        </div>
      </main>
    </section>

    <section id="create-section" class="hidden container" style="padding: 3rem 1rem; min-height: 90vh; display: flex; justify-content: center;">
        <div class="glass-card" style="width: 100%; max-width: 900px; background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%); backdrop-filter: blur(20px); border-radius: 24px; border: 1px solid rgba(255, 255, 255, 0.18); padding: 3rem; color: white; box-shadow: 0 20px 50px rgba(0,0,0,0.3);">
            <div style="text-align: center; margin-bottom: 40px;">
                <h2 style="font-size: 2.5rem; font-weight: 800; margin: 0;">📝 Nueva Encuesta</h2>
            </div>
            <form id="create-survey-form">
                <div style="margin-bottom: 25px;">
                    <label class="label-hd">Título</label>
                    <input type="text" id="new-survey-title" required class="input-glass" placeholder="Ej: Opiniones sobre el nuevo parche">
                </div>
                <div style="margin-bottom: 25px;">
                    <label class="label-hd">Descripción</label>
                    <textarea id="new-survey-desc" required rows="3" class="input-glass" placeholder="Describe brevemente de qué trata..."></textarea>
                </div>
                <hr style="border:0; height: 1px; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent); margin: 40px 0;">
                <h3 style="color:#55efc4; margin-bottom:20px; font-size: 1.5rem; text-shadow: 0 2px 4px rgba(0,0,0,0.5);">Preguntas</h3>
                <div id="questions-list" style="margin-bottom: 25px; display: flex; flex-direction: column; gap: 10px;"></div>
                
                <div style="background: rgba(0,0,0,0.2); padding: 25px; border-radius: 16px; border: 1px solid rgba(255,255,255,0.05);">
                    <div style="display:flex; gap:15px; margin-bottom:15px;">
                        <div style="flex: 2;">
                            <label style="font-size: 0.9rem; color: #ccc; margin-bottom: 5px; display: block;">Pregunta</label>
                            <input type="text" id="q-text" placeholder="¿Qué quieres preguntar?" class="input-glass">
                        </div>
                        <div style="flex: 2;">
                            <label style="font-size: 0.9rem; color: #ccc; margin-bottom: 5px; display: block;">Opciones</label>
                            <input type="text" id="q-options" placeholder="Ej: Sí, No, Tal vez" class="input-glass">
                        </div>
                    </div>
                    <button type="button" onclick="window.addQuestionToLocal()" style="background: #6c5ce7; color: white; border: none; padding: 12px 20px; border-radius: 8px; cursor: pointer; font-weight: bold; width: 100%; transition: background 0.3s;" onmouseover="this.style.background='#a29bfe'" onmouseout="this.style.background='#6c5ce7'">
                        ➕ Agregar a la lista
                    </button>
                </div>

                <div style="margin-top: 40px; display: flex; gap: 15px; justify-content: flex-end; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 20px;">
                    <button type="button" onclick="window.showDashboard()" style="background: transparent; color: #ff7675; border: 1px solid #ff7675; padding: 12px 25px; border-radius: 10px; cursor: pointer; font-weight: bold; transition: all 0.3s;" onmouseover="this.style.background='rgba(255,118,117,0.1)'" onmouseout="this.style.background='transparent'">Cancelar</button>
                    <button type="submit" style="background: linear-gradient(45deg, #00b894, #00cec9); color: white; border: none; padding: 12px 40px; border-radius: 10px; cursor: pointer; font-weight: bold; font-size: 1.1rem; box-shadow: 0 5px 15px rgba(0, 184, 148, 0.3); transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.05)'" onmouseout="this.style.transform='scale(1)'">💾 Publicar Encuesta</button>
                </div>
            </form>
        </div>
    </section>

    <section id="respond-section" class="hidden" style="position: fixed; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.85); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 99;">
        <div class="auth-card" style="width: 100%; max-width: 600px; background: #2d3436; padding: 30px; border-radius: 20px; border: 1px solid #444; box-shadow: 0 20px 60px rgba(0,0,0,0.5);">
            <h2 id="respond-title" style="color:white; margin-top:0; text-align: center; font-size: 2rem; margin-bottom: 20px;">Responder</h2>
            <form id="respond-form">
                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:15px; margin-bottom:25px;">
                    <div>
                        <label style="display:block; color:#ccc; margin-bottom:5px; font-size:0.9rem;">Tiempo Jugado</label>
                        <select id="time-played" class="input-glass" style="background:rgba(0,0,0,0.5); color:white;"><option>Menos de 10h</option><option>+10h</option></select>
                    </div>
                    <div>
                        <label style="display:block; color:#ccc; margin-bottom:5px; font-size:0.9rem;">Plataforma</label>
                        <select id="platform-used" class="input-glass" style="background:rgba(0,0,0,0.5); color:white;"><option>PC</option><option>Consola</option><option>Móvil</option></select>
                    </div>
                </div>
                <div id="respond-questions-container" style="display:flex; flex-direction:column; gap:15px;"></div>
                <div style="margin-top:30px; display:flex; gap:15px;">
                    <button type="button" onclick="window.showDashboard()" style="flex:1; background:transparent; border:1px solid #ff7675; padding:12px; color:#ff7675; border-radius:8px; cursor:pointer; font-weight:bold;">Cancelar</button>
                    <button type="submit" style="flex:2; background:#00b894; border:none; padding:12px; color:white; border-radius:8px; cursor:pointer; font-weight:bold; font-size:1.1rem;">Enviar Respuesta</button>
                </div>
            </form>
        </div>
    </section>

    <section id="results-section" class="hidden container" style="padding: 3rem 2rem; min-height: 100vh;">
        <button onclick="window.showDashboard()" style="background:rgba(255,255,255,0.1); color:white; border:1px solid rgba(255,255,255,0.2); padding:10px 25px; border-radius:30px; cursor:pointer; margin-bottom:30px; font-weight:bold; transition:background 0.3s;" onmouseover="this.style.background='rgba(255,255,255,0.2)'" onmouseout="this.style.background='rgba(255,255,255,0.1)'">⬅ Volver al Dashboard</button>
        <div id="results-content" class="results-card" style="background:rgba(30,30,40,0.8); padding:3rem; border-radius:24px; border:1px solid rgba(255,255,255,0.1); box-shadow: 0 10px 40px rgba(0,0,0,0.4);"></div>
    </section>
  `;
  setupEventListeners();
  checkAuth();
}

// ==========================================
// 2. LISTENERS
// ==========================================
function setupEventListeners() {
  document.getElementById('login-form')?.addEventListener('submit', handleLogin);
  document.getElementById('register-form')?.addEventListener('submit', handleRegister);
  document.getElementById('logout-btn')?.addEventListener('click', handleLogout);
  document.getElementById('create-survey-form')?.addEventListener('submit', handleCreateSurveySubmit);
  document.getElementById('respond-form')?.addEventListener('submit', handleRespondSubmit);
  document.getElementById('show-register-link')?.addEventListener('click', (e) => { e.preventDefault(); showRegister(); });
  document.getElementById('show-login-link')?.addEventListener('click', (e) => { e.preventDefault(); showLogin(); });
}

// ==========================================
// 3. DASHBOARD
// ==========================================

async function loadSurveys() {
  try {
    const res = await fetch(`${API_URL}/surveys`, { headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` } });
    if (res.ok) { displaySurveys(await res.json()); }
  } catch (error) { console.error(error); }
}

function displaySurveys(surveys) {
  const container = document.getElementById('surveys-container');
  if (surveys.length === 0) { container.innerHTML = '<p style="color:white; text-align:center; font-size:1.2rem; opacity:0.7;">No hay encuestas disponibles en este momento.</p>'; return; }

  const isCrafter = currentUser && (currentUser.role === 'ROLE_CRAFTER' || currentUser.role === 'CRAFTER');

  container.innerHTML = surveys.map(survey => `
    <div class="survey-row" style="
        padding: 1.8rem 2.5rem; 
        border-radius: 16px; 
        display: flex; 
        justify-content: space-between; 
        align-items: center;
        margin-bottom: 10px;
    ">
      
      <div style="display: flex; flex-direction: column; gap: 8px; flex: 1;">
        <div style="display: flex; align-items: center; gap: 15px;">
            <div style="background: rgba(255,255,255,0.1); width: 35px; height: 35px; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-weight: bold; color: white; border: 1px solid rgba(255,255,255,0.1);">${survey.surveyId}</div>
            <span style="background: linear-gradient(45deg, #00b894, #00cec9); color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 800; letter-spacing: 1px; text-transform: uppercase;">${survey.estado || 'ACTIVA'}</span>
        </div>
        <h3 style="margin: 5px 0 0 0; color: white; font-size: 1.6rem; font-weight: 800; text-shadow: 0 2px 5px rgba(0,0,0,0.3);">${survey.titulo}</h3>
        <p style="margin: 0; color: rgba(255,255,255,0.8); font-size: 1rem; line-height: 1.4;">${survey.descripcion || 'Sin descripción'}</p>
        <p style="margin: 5px 0 0 0; color: #a29bfe; font-size: 0.9rem; font-weight: 500;">👥 ${survey.totalRespuestas || 0} respuestas</p>
      </div>

      <div style="display: flex; gap: 15px; margin-left: 30px;">
        ${isCrafter ? `
            <button onclick="window.viewSurveyResults(${survey.surveyId})" style="background: rgba(108, 92, 231, 0.2); border: 1px solid #a29bfe; color: #a29bfe; padding: 10px 20px; border-radius: 10px; cursor:pointer; font-weight:bold; transition: all 0.3s;" onmouseover="this.style.background='#a29bfe'; this.style.color='white'" onmouseout="this.style.background='rgba(108, 92, 231, 0.2)'; this.style.color='#a29bfe'">📊 Resultados</button>
            <button onclick="window.deleteSurvey(${survey.surveyId})" style="background: rgba(255, 118, 117, 0.1); border: 1px solid #ff7675; color: #ff7675; padding: 10px 20px; border-radius: 10px; cursor:pointer; font-weight:bold; transition: all 0.3s;" onmouseover="this.style.background='#ff7675'; this.style.color='white'" onmouseout="this.style.background='rgba(255, 118, 117, 0.1)'; this.style.color='#ff7675'">Eliminar</button>
        ` : `
            <button onclick="window.openRespondModal(${survey.surveyId})" style="background: linear-gradient(45deg, #0984e3, #74b9ff); color: white; padding: 12px 35px; border:none; border-radius: 10px; cursor:pointer; font-weight:bold; font-size: 1rem; box-shadow: 0 5px 15px rgba(9, 132, 227, 0.4); transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.05)'" onmouseout="this.style.transform='scale(1)'">Responder</button>
        `}
      </div>
    </div>
  `).join('');
}

// ==========================================
// 4. ACCIONES
// ==========================================

window.showCreateScreen = function() { hideAll(); document.getElementById('create-section').classList.remove('hidden'); newSurveyQuestions = []; renderQuestionsList(); window.scrollTo(0,0); }
window.addQuestionToLocal = function() {
    const t = document.getElementById('q-text').value;
    const o = document.getElementById('q-options').value;
    if(t && o) { newSurveyQuestions.push({texto:t, tipoPregunta:'SINGLE_CHOICE', opciones:o.split(',')}); renderQuestionsList(); document.getElementById('q-text').value=''; document.getElementById('q-options').value=''; } else { alert("Faltan datos"); }
}
function renderQuestionsList() {
    const list = document.getElementById('questions-list');
    if(newSurveyQuestions.length === 0) { list.innerHTML = '<p style="text-align:center; color:rgba(255,255,255,0.4); font-style:italic; border: 2px dashed rgba(255,255,255,0.1); padding: 20px; border-radius: 12px;">Aún no has agregado preguntas a esta encuesta.</p>'; return; }
    list.innerHTML = newSurveyQuestions.map((q,i)=>`
        <div style="background:rgba(255,255,255,0.05); padding:15px; margin-bottom:10px; border-radius:10px; color:white; border:1px solid rgba(255,255,255,0.1); display:flex; justify-content:space-between; align-items:center;">
            <div>
                <strong style="color:#55efc4; font-size:1.1rem;">${i+1}. ${q.texto}</strong><br>
                <small style="color:#aaa;">Opciones: ${q.opciones.join(' / ')}</small>
            </div>
            <button onclick="window.removeQuestion(${i})" style="background:none; border:none; color:#ff7675; font-size:1.5rem; cursor:pointer;">&times;</button>
        </div>`).join('');
}
window.removeQuestion = function(i) { newSurveyQuestions.splice(i,1); renderQuestionsList(); }

async function handleCreateSurveySubmit(e) {
    e.preventDefault();
    if(newSurveyQuestions.length === 0) { alert("Agrega preguntas primero"); return; }
    const btn = e.target.querySelector('button[type="submit"]'); btn.innerHTML="⏳ Guardando..."; btn.disabled=true;
    const p = { titulo: document.getElementById('new-survey-title').value, descripcion: document.getElementById('new-survey-desc').value, juegoId: 1, tipoEncuesta: 'JUEGO', preguntas: newSurveyQuestions };
    try {
        const r = await fetch(`${API_URL}/surveys`, { method:'POST', headers:{'Content-Type':'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}, body:JSON.stringify(p)});
        if(r.ok){ alert("✅ Encuesta Publicada"); window.showDashboard(); } else { alert('Error al crear'); }
    } catch(err){ console.error(err); } finally { btn.innerHTML="💾 Publicar Encuesta"; btn.disabled=false; }
}

// RESPONDER
window.openRespondModal = async function(id) {
    const res = await fetch(`${API_URL}/surveys/${id}`, { headers: {'Authorization':`Bearer ${localStorage.getItem('token')}`} });
    const s = await res.json();
    document.getElementById('respond-title').textContent = s.titulo;
    document.getElementById('respond-form').dataset.surveyId = id;
    document.getElementById('respond-questions-container').innerHTML = s.preguntas.map(q=>`
        <div style="margin-bottom:20px; background:rgba(0,0,0,0.2); padding:20px; border-radius:12px; border:1px solid rgba(255,255,255,0.05);">
            <p style="color:white; margin-bottom:15px; font-weight:bold; font-size:1.1rem;">${q.textoPregunta}</p>
            <div style="display:flex; flex-direction:column; gap:10px;">
            ${q.opciones.map(o=>`
                <label style="display:flex; align-items:center; padding:12px; background:rgba(255,255,255,0.05); border-radius:8px; cursor:pointer; transition:background 0.2s;" onmouseover="this.style.background='rgba(255,255,255,0.1)'" onmouseout="this.style.background='rgba(255,255,255,0.05)'">
                    <input type="radio" name="q_${q.questionId}" value="${o.optionId}" required style="accent-color:#00b894; transform:scale(1.2);"> 
                    <span style="margin-left:12px; color:#ddd;">${o.textoOpcion}</span>
                </label>`).join('')}
            </div>
        </div>
    `).join('');
    hideAll(); document.getElementById('respond-section').classList.remove('hidden');
}
async function handleRespondSubmit(e) {
    e.preventDefault();
    const id = e.target.dataset.surveyId;
    const fd = new FormData(e.target);
    const resp = [];
    for(let [k,v] of fd.entries()) if(k.startsWith('q_')) resp.push({questionId: parseInt(k.split('_')[1]), optionId: parseInt(v)});
    
    try {
        await fetch(`${API_URL}/surveys/${id}/respond`, { method:'POST', headers:{'Content-Type':'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}, body:JSON.stringify({respuestas:resp, tiempoJugadoJuego:'10h', plataformaUsada:'PC'}) });
        alert("¡Gracias por tu respuesta!"); window.showDashboard();
    } catch(err){ console.error(err); }
}

// RESULTADOS
window.viewSurveyResults = async function(id) {
    const res = await fetch(`${API_URL}/surveys/${id}/results`, { headers: {'Authorization':`Bearer ${localStorage.getItem('token')}`} });
    const d = await res.json();
    hideAll(); document.getElementById('results-section').classList.remove('hidden');
    let html = `<div style="text-align:center; margin-bottom:40px;"><h2 style="color:white; font-size:2.5rem; margin-bottom:10px;">${d.titulo}</h2><div style="display:inline-block; padding:5px 20px; border:1px solid #a29bfe; border-radius:20px; color:#a29bfe; font-weight:bold;">Total Votos: ${d.totalRespuestas}</div></div>`;
    d.preguntas.forEach(q=>{
        html += `<div style="margin-bottom:30px; background:rgba(0,0,0,0.2); padding:25px; border-radius:16px; border:1px solid rgba(255,255,255,0.05);"><h3 style="color:white; margin-bottom:20px; display:flex; align-items:center; gap:10px;"><span style="width:5px; height:25px; background:#6c5ce7; display:block; border-radius:5px;"></span>${q.textoPregunta}</h3>`;
        q.opciones.forEach(o=>{
            const pct = o.porcentaje || 0;
            let col = pct>50?'#00b894':(pct<20?'#ff7675':'#6c5ce7');
            html+=`<div style="margin-bottom:15px;"><div style="display:flex; justify-content:space-between; color:#ccc; font-size:0.95rem; margin-bottom:5px;"><span>${o.textoOpcion}</span><span style="color:${col}; font-weight:bold;">${o.votos} (${pct}%)</span></div>
                   <div style="background:rgba(255,255,255,0.1); height:10px; border-radius:5px; overflow:hidden;"><div style="background:${col}; width:${pct}%; height:100%; border-radius:5px; transition:width 1s ease;"></div></div></div>`;
        });
        html+='</div>';
    });
    document.getElementById('results-content').innerHTML = html;
}

window.deleteSurvey = async function(id) { if(confirm("¿Eliminar permanentemente?")) { await fetch(`${API_URL}/surveys/${id}`, {method:'DELETE', headers:{'Authorization':`Bearer ${localStorage.getItem('token')}`}}); loadSurveys(); } }

// NAV
window.showDashboard = function() { hideAll(); document.getElementById('dashboard-section').classList.remove('hidden'); window.scrollTo(0,0); if(currentUser){ document.getElementById('user-name').textContent=currentUser.nombre; if(currentUser.role.includes('CRAFTER')) document.getElementById('create-btn').classList.remove('hidden'); else document.getElementById('create-btn').classList.add('hidden'); loadSurveys(); } }
function showLogin() { 
    hideAll(); 
    document.getElementById('auth-section').classList.remove('hidden'); 
    document.getElementById('login-container').classList.remove('hidden'); 
    document.getElementById('register-container').classList.add('hidden');
}
function showRegister() { 
    document.getElementById('auth-section').classList.remove('hidden');
    document.getElementById('login-container').classList.add('hidden'); 
    document.getElementById('register-container').classList.remove('hidden'); 
}
function hideAll() { ['auth-section','dashboard-section','results-section','create-section','respond-section'].forEach(id=>document.getElementById(id).classList.add('hidden')); }

// AUTH
function checkAuth() { const t=localStorage.getItem('token'); const u=localStorage.getItem('user'); if(t&&u){ try{ currentUser=JSON.parse(u); window.showDashboard(); }catch(e){ handleLogout(); } } else showLogin(); }
async function handleLogin(e) { e.preventDefault(); const res=await fetch(`${API_URL}/auth/login`, {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({email:document.getElementById('login-email').value, password:document.getElementById('login-password').value})}); if(res.ok){ const d=await res.json(); if(d.token){ localStorage.setItem('token',d.token); 
        // CAMBIO AQUÍ: Usamos data.username que viene del backend
        const usuarioAdaptado = { 
            nombre: d.username || (d.role.includes('CRAFTER') ? 'Admin' : 'Player'), // Fallback si no hay username
            role: d.role, 
            email: document.getElementById('login-email').value 
        };
        localStorage.setItem('user',JSON.stringify(usuarioAdaptado)); currentUser=usuarioAdaptado; window.showDashboard(); } } else alert('Error credenciales'); }
async function handleRegister(e) { e.preventDefault(); const p={username:document.getElementById('register-name').value, email:document.getElementById('register-email').value, password:document.getElementById('register-password').value, role:document.getElementById('register-role').value}; const res=await fetch(`${API_URL}/auth/register`, {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(p)}); if(res.ok){ alert('Ok'); showLogin(); } }
function handleLogout() { localStorage.clear(); currentUser=null; showLogin(); }

document.addEventListener('DOMContentLoaded', renderApp);