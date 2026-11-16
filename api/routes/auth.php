<?php
// ============================================================
//  SurveyCraft - Rutas de Autenticación
//  POST /api/auth/register
//  POST /api/auth/login
// ============================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../helpers/response.php';

function handleAuth(string $action): void {
    $pdo = getDB();

    // ---- REGISTER ----
    if ($action === 'register' && getMethod() === 'POST') {
        $body = getRequestBody();

        $email    = trim($body['email']    ?? '');
        $password = trim($body['password'] ?? '');
        $username = trim($body['username'] ?? '');
        $roleName = strtoupper(trim($body['role'] ?? 'PLAYER'));

        if (!$email || !$password || !$username) {
            jsonError('Email, password y username son requeridos.');
        }
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            jsonError('Email inválido.');
        }

        // Verificar si ya existe
        $check = $pdo->prepare("SELECT user_id FROM usuarios WHERE email = ?");
        $check->execute([$email]);
        if ($check->fetch()) {
            jsonError('El email ya está registrado.', 409);
        }

        // Buscar rol
        $roleFull = 'ROLE_' . $roleName;
        $roleStmt = $pdo->prepare("SELECT role_id FROM roless WHERE nombre = ?");
        $roleStmt->execute([$roleFull]);
        $role = $roleStmt->fetch();
        if (!$role) {
            jsonError("Rol no encontrado: $roleFull");
        }

        // Crear usuario (BCrypt — compatible con Spring Security)
        $hash = password_hash($password, PASSWORD_BCRYPT);
        $ins  = $pdo->prepare("INSERT INTO usuarios (username, email, password_hash, role_id) VALUES (?, ?, ?, ?)");
        $ins->execute([$username, $email, $hash, $role['role_id']]);

        jsonResponse(['message' => 'Registro exitoso'], 201);
    }

    // ---- LOGIN ----
    if ($action === 'login' && getMethod() === 'POST') {
        $body = getRequestBody();

        $email    = trim($body['email']    ?? '');
        $password = trim($body['password'] ?? '');

        if (!$email || !$password) {
            jsonError('Email y password son requeridos.');
        }

        $stmt = $pdo->prepare("
            SELECT u.user_id, u.username, u.email, u.password_hash, r.nombre AS role_nombre
            FROM usuarios u
            JOIN roless r ON u.role_id = r.role_id
            WHERE u.email = ?
        ");
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if (!$user || !password_verify($password, $user['password_hash'])) {
            jsonError('Credenciales inválidas.', 401);
        }

        // Token = "userId:ROLE_NAME"  (mismo formato que Spring Boot)
        $token = $user['user_id'] . ':' . $user['role_nombre'];

        jsonResponse([
            'token' => $token,
            'role'  => $user['role_nombre'],
        ]);
    }

    jsonError('Ruta de auth no encontrada.', 404);
}
