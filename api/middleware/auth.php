<?php
// ============================================================
//  SurveyCraft - Middleware de autenticación por token
//  Token format: "userId:ROLE_NAME"  (igual que en Spring Boot)
// ============================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../helpers/response.php';

function getAuthUser(): ?array {
    $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    if (!str_starts_with($header, 'Bearer ')) {
        return null;
    }

    $token = substr($header, 7);
    $parts = explode(':', $token, 2);
    if (count($parts) !== 2) return null;

    [$userId, $role] = $parts;
    if (!is_numeric($userId)) return null;

    try {
        $pdo = getDB();
        $stmt = $pdo->prepare("
            SELECT u.user_id, u.username, u.email, r.nombre AS role_nombre
            FROM usuarios u
            JOIN roless r ON u.role_id = r.role_id
            WHERE u.user_id = ?
        ");
        $stmt->execute([(int)$userId]);
        $user = $stmt->fetch();

        if ($user && $user['role_nombre'] === $role) {
            return $user;
        }
    } catch (Exception $e) {
        // DB error
    }
    return null;
}

function requireAuth(): array {
    $user = getAuthUser();
    if (!$user) {
        jsonError('No autenticado. Proporciona un token válido.', 401);
    }
    return $user;
}

function requireRole(string $role): array {
    $user = requireAuth();
    if ($user['role_nombre'] !== $role) {
        jsonError('Acceso denegado. Rol insuficiente.', 403);
    }
    return $user;
}
