<?php
// ============================================================
//  SurveyCraft - Router principal (index.php)
//  Punto de entrada único de la API PHP
//
//  WAMP/XAMPP:  Coloca la carpeta surveycraft-php/ dentro de
//               C:\wamp64\www\  o  C:\xampp\htdocs\
//  Acceso:      http://localhost/surveycraft-php/api/surveys
// ============================================================

require_once __DIR__ . '/helpers/response.php';
require_once __DIR__ . '/routes/auth.php';
require_once __DIR__ . '/routes/games.php';
require_once __DIR__ . '/routes/surveys.php';

// 1. Headers CORS (siempre primero)
setCorsHeaders();

// 2. Manejar preflight OPTIONS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

// 3. Parsear la URL — funciona con cualquier nombre de carpeta en WAMP/XAMPP
$requestUri = $_SERVER['REQUEST_URI'] ?? '/';
$path       = parse_url($requestUri, PHP_URL_PATH);
$path       = trim($path, '/');
$segments   = $path !== '' ? explode('/', $path) : [];

// Buscar dónde aparece "api" en la URL (funciona con subdirectorios arbitrarios)
// Ej: /surveycraft-php/api/auth/login → busca índice de "api"
// Ej: /api/auth/login → funciona igual
$apiIdx = array_search('api', $segments);
if ($apiIdx === false) {
    jsonError('Endpoint no encontrado.', 404);
}

// Recortar todo antes de "api" (inclusive) → queda [resource, ...subSegments]
$segments = array_values(array_slice($segments, $apiIdx + 1));

// 5. Router principal
$resource = $segments[0] ?? '';
array_shift($segments); // quitar el recurso principal

switch ($resource) {
    // --- AUTH ---
    case 'auth':
        $action = $segments[0] ?? '';
        handleAuth($action);
        break;

    // --- GAMES ---
    case 'games':
        $id = isset($segments[0]) && is_numeric($segments[0]) ? (int)$segments[0] : null;
        handleGames($id);
        break;

    // --- SURVEYS ---
    case 'surveys':
        handleSurveys($segments);
        break;

    default:
        jsonError("Recurso '$resource' no encontrado.", 404);
}
