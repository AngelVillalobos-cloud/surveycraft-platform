import './style.css';

const API_URL = 'http://localhost:8080/api';
let currentUser = null;

// Render the app
function renderApp() {
  const app = document.getElementById('app');
  app.innerHTML = `
    <!-- Auth Section -->
    <section id="auth-section">
      <div id="login-container" class="auth-container">
        <div class="auth-card">
          <img src="/logo.svg" alt="SurveyCraft Logo" style="width: 100px; height: 100px; margin: 0 auto 2rem;">
          <h1>SurveyCraft</h1>
          <p class="subtitle">Plataforma de Encuestas Gaming</p>
          
          <form id="login-form">
            <div class="form-group">
              <label for="login-email">📧 Email</label>
              <input type="email" id="login-email" required>
            </div>
            
            <div class="form-group">
              <label for="login-password">🔒 Contraseña</label>
              <input type="password" id="login-password" required>
            </div>
            
            <button type="submit" class="btn btn-primary btn-block">Iniciar Sesión</button>
          </form>
          
          <p class="auth-link">
            ¿No tienes cuenta? <a href="#" id="show-register-link">Regístrate</a>
          </p>
        </div>
      </div>

      <div id="register-container" class="auth-container hidden">
        <div class="auth-card">
          <img src="/logo.svg" alt="SurveyCraft Logo" style="width: 100px; height: 100px; margin: 0 auto 2rem;">
          <h1>Crear Cuenta</h1>
          <p class="subtitle">Únete a la comunidad</p>
          
          <form id="register-form">
            <div class="form-group">
              <label for="register-name">👤 Nombre</label>
              <input type="text" id="register-name" required>
            </div>
            
            <div class="form-group">
              <label for="register-email">📧 Email</label>
              <input type="email" id="register-email" required>
            </div>
            
            <div class="form-group">
              <label for="register-password">🔒 Contraseña</label>
              <input type="password" id="register-password" required>
            </div>
            
            <div class="form-group">
              <label for="register-role">🎭 Rol</label>
              <select id="register-role" required>
                <option value="">Selecciona un rol</option>
                <option value="PLAYER">🎮 Jugador</option>
                <option value="CRAFTER">🔨 Creador de Encuestas</option>
              </select>
            </div>
            
            <button type="submit" class="btn btn-primary btn-block">Registrarse</button>
          </form>
          
          <p class="auth-link">
            ¿Ya tienes cuenta? <a href="#" id="show-login-link">Inicia Sesión</a>
          </p>
        </div>
      </div>
    </section>

    <section id="dashboard-section" class="hidden">
      <nav class="navbar">
        <div class="navbar-brand">
          <img src="/logo.svg" alt="SurveyCraft" class="logo-icon">
          <span>SurveyCraft</span>
        </div>
        <div class="navbar-user">
          <span>👤 <span id="user-name"></span></span>
          <button id="logout-btn" class="btn btn-secondary">Cerrar Sesión</button>
        </div>
      </nav>

      <main class="container">
        <div class="header">
          <h2>📊 Mis Encuestas</h2>
        </div>
        <div id="surveys-container" class="surveys-grid">
          <p class="loading">Cargando encuestas...</p>
        </div>
      </main>
    </section>
  `;
  
  setupEventListeners();
  checkAuth();
}

function setupEventListeners() {
  const loginForm = document.getElementById('login-form');
  if (loginForm) loginForm.addEventListener('submit', handleLogin);
  
  const registerForm = document.getElementById('register-form');
  if (registerForm) registerForm.addEventListener('submit', handleRegister);
  
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);
  
  const showRegisterLink = document.getElementById('show-register-link');
  if (showRegisterLink) showRegisterLink.addEventListener('click', (e) => {
    e.preventDefault();
    showRegister();
  });
  
  const showLoginLink = document.getElementById('show-login-link');
  if (showLoginLink) showLoginLink.addEventListener('click', (e) => {
    e.preventDefault();
    showLogin();
  });
}

function checkAuth() {
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');
  
  if (token && user) {
    currentUser = JSON.parse(user);
    showDashboard();
  } else {
    showLogin();
  }
}

function showLogin() {
  document.getElementById('auth-section').classList.remove('hidden');
  document.getElementById('dashboard-section').classList.add('hidden');
  document.getElementById('login-container').classList.remove('hidden');
  document.getElementById('register-container').classList.add('hidden');
}

function showRegister() {
  document.getElementById('login-container').classList.add('hidden');
  document.getElementById('register-container').classList.remove('hidden');
}

function showDashboard() {
  document.getElementById('auth-section').classList.add('hidden');
  document.getElementById('dashboard-section').classList.remove('hidden');
  document.getElementById('user-name').textContent = currentUser.nombre;
  loadSurveys();
}

async function handleLogin(e) {
  e.preventDefault();
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-password').value;

  try {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      currentUser = data.user;
      showDashboard();
    } else {
      alert('❌ Error al iniciar sesión');
    }
  } catch (error) {
    console.error('Error:', error);
    alert('❌ Error de conexión');
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const nombre = document.getElementById('register-name').value;
  const email = document.getElementById('register-email').value;
  const password = document.getElementById('register-password').value;
  const role = document.getElementById('register-role').value;

  try {
    const response = await fetch(`${API_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nombre, email, password, role })
    });

    if (response.ok) {
      alert('✅ Registro exitoso');
      showLogin();
    } else {
      alert('❌ Error al registrarse');
    }
  } catch (error) {
    console.error('Error:', error);
    alert('❌ Error de conexión');
  }
}

function handleLogout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  currentUser = null;
  showLogin();
}

async function loadSurveys() {
  try {
    const response = await fetch(`${API_URL}/surveys`, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    });

    if (response.ok) {
      const surveys = await response.json();
      displaySurveys(surveys);
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

function displaySurveys(surveys) {
  const container = document.getElementById('surveys-container');
  
  if (surveys.length === 0) {
    container.innerHTML = '<p class="no-data">No hay encuestas disponibles</p>';
    return;
  }

  container.innerHTML = surveys.map(survey => `
    <div class="survey-card">
      <h4>${survey.titulo}</h4>
      <p>${survey.descripcion || 'Sin descripción'}</p>
    </div>
  `).join('');
}

// Initialize
document.addEventListener('DOMContentLoaded', renderApp);
