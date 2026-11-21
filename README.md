# SurveyCraft — PHP + MySQL (para WAMP / XAMPP / Laragon)

> Versión migrada del proyecto original Java + Oracle + Docker.  
> Sin Docker. Sin Java. Sin Maven. Solo PHP + MySQL.

---

## 📁 Estructura del proyecto

```
surveycraft-php/
├── index.html          ← Frontend (SPA, sin Vite)
├── main.js             ← Toda la lógica del frontend
├── style.css           ← Estilos (idénticos al original)
├── .htaccess           ← Routing de Apache (mod_rewrite)
├── schema.sql          ← Script para crear la base de datos
└── api/
    ├── index.php           ← Router principal de la API
    ├── config/
    │   └── database.php    ← Conexión PDO a MySQL ← EDITAR AQUÍ
    ├── helpers/
    │   └── response.php    ← Funciones JSON/CORS
    ├── middleware/
    │   └── auth.php        ← Verificación de token
    └── routes/
        ├── auth.php        ← /api/auth/register y /api/auth/login
        ├── surveys.php     ← /api/surveys (10 endpoints)
        └── games.php       ← /api/games (5 endpoints)
```

---

## 🚀 Instalación paso a paso

### 1. Requisitos
- **WAMP** (Windows) → [wampserver.com](https://www.wampserver.com)  
  o **XAMPP** → [apachefriends.org](https://www.apachefriends.org)  
  o **Laragon** → [laragon.org](https://laragon.org) *(recomendado por ser más moderno)*
- PHP 8.1 o superior (incluido en cualquiera de los tres)
- MySQL (incluido)

### 2. Copiar la carpeta

**En WAMP:**
```
C:\wamp64\www\surveycraft-php\
```

**En XAMPP:**
```
C:\xampp\htdocs\surveycraft-php\
```

**En Laragon:**
```
C:\laragon\www\surveycraft-php\
```

### 3. Crear la base de datos

1. Abre **phpMyAdmin** → `http://localhost/phpmyadmin`
2. Crea una base de datos llamada `surveycraft`  
   *(o crea cualquier nombre y actualiza `database.php`)*
3. Haz clic en la base de datos → pestaña **SQL**
4. Pega el contenido de `schema.sql` y ejecuta

> Las tablas y los roles (`ROLE_CRAFTER`, `ROLE_PLAYER`) se crean automáticamente.

### 4. Configurar la conexión

Edita el archivo `api/config/database.php`:

```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'surveycraft');   // nombre de tu base de datos
define('DB_USER', 'root');          // usuario MySQL (WAMP/XAMPP por defecto: root)
define('DB_PASS', '');              // contraseña (WAMP/XAMPP por defecto: vacía)
define('DB_PORT', '3306');
```

> **WAMP:** el password de root suele estar vacío  
> **XAMPP:** igual, vacío por defecto  
> **Laragon:** el password por defecto es `root`

### 5. Habilitar mod_rewrite en Apache

**WAMP:** Clic izquierdo en el ícono de la bandeja → Apache → Apache modules → ✅ `rewrite_module`

**XAMPP:** Ya viene habilitado por defecto.

**Laragon:** Ya viene habilitado por defecto.

### 6. Abrir la app

```
http://localhost/surveycraft-php/
```

---

## ✅ Funcionalidades migradas

| Funcionalidad | Estado |
|---|---|
| Registro de usuarios (CRAFTER / PLAYER) | ✅ |
| Login con token | ✅ |
| Ver todas las encuestas | ✅ |
| Ver mis encuestas | ✅ |
| Crear encuesta con preguntas y opciones | ✅ |
| Editar encuesta | ✅ |
| Eliminar encuesta | ✅ |
| Cambiar estado de encuesta | ✅ |
| Responder encuesta | ✅ |
| Ver resultados con porcentajes | ✅ |
| Crear juegos/categorías | ✅ |
| Listar juegos | ✅ |

---

## 🔐 Compatibilidad de contraseñas

Las contraseñas se guardan con **BCrypt** (igual que Spring Security).  
Si tienes usuarios del sistema anterior y exportas la tabla `usuarios`, las contraseñas funcionarán sin necesidad de resetearlas.

---

## 🛠️ Solución de problemas

| Problema | Solución |
|---|---|
| `404 Not Found` en rutas API | Verificar que `mod_rewrite` esté activo y que `.htaccess` esté en la carpeta raíz |
| `Connection refused` a MySQL | Verificar que WAMP/XAMPP esté corriendo y MySQL activo |
| `Access denied for user 'root'` | Revisar usuario/contraseña en `database.php` |
| Pantalla en blanco | Habilitar errores PHP: agregar `ini_set('display_errors', 1);` al inicio de `api/index.php` temporalmente |
