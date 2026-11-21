<?php
// ============================================================
//  SurveyCraft - Rutas de Juegos/Videojuegos
//  GET    /api/games
//  GET    /api/games/{id}
//  POST   /api/games
//  PUT    /api/games/{id}
//  DELETE /api/games/{id}
// ============================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../middleware/auth.php';

function handleGames(?int $id = null): void {
    $pdo    = getDB();
    $method = getMethod();

    // GET /api/games
    if ($id === null && $method === 'GET') {
        $rows = $pdo->query("
            SELECT v.id, v.titulo, v.descripcion, c.id AS categoria_id, c.id AS categoriaId,
                   c.nombre_categoria, c.nombre_categoria AS nombreCategoria
            FROM videojuegos v
            JOIN categories c ON v.categoria_id = c.id
            ORDER BY v.titulo
        ")->fetchAll();
        jsonResponse($rows);
    }

    // GET /api/games/{id}
    if ($id !== null && $method === 'GET') {
        $stmt = $pdo->prepare("
            SELECT v.id, v.titulo, v.descripcion, c.id AS categoria_id, c.id AS categoriaId,
                   c.nombre_categoria, c.nombre_categoria AS nombreCategoria
            FROM videojuegos v
            JOIN categories c ON v.categoria_id = c.id
            WHERE v.id = ?
        ");
        $stmt->execute([$id]);
        $game = $stmt->fetch();
        if (!$game) jsonError('Juego no encontrado.', 404);
        jsonResponse($game);
    }

    // POST /api/games  — Solo CRAFTER
    if ($id === null && $method === 'POST') {
        requireRole('ROLE_CRAFTER');
        $body           = getRequestBody();
        $titulo         = trim($body['titulo']         ?? '');
        $descripcion    = trim($body['descripcion']    ?? '');
        $categoriaId    = $body['categoriaId']         ?? null;
        $nombreCategoria = trim($body['nombreCategoria'] ?? '');

        if (!$titulo) jsonError('El título es requerido.');

        // Resolver categoría
        if ($categoriaId) {
            $catStmt = $pdo->prepare("SELECT id FROM categories WHERE id = ?");
            $catStmt->execute([$categoriaId]);
            if (!$catStmt->fetch()) jsonError('Categoría no encontrada.', 404);
        } elseif ($nombreCategoria) {
            $catStmt = $pdo->prepare("SELECT id FROM categories WHERE nombre_categoria = ?");
            $catStmt->execute([$nombreCategoria]);
            $cat = $catStmt->fetch();
            if ($cat) {
                $categoriaId = $cat['id'];
            } else {
                $pdo->prepare("INSERT INTO categories (nombre_categoria) VALUES (?)")->execute([$nombreCategoria]);
                $categoriaId = (int)$pdo->lastInsertId();
            }
        } else {
            jsonError('Debe proporcionar categoriaId o nombreCategoria.');
        }

        $pdo->prepare("INSERT INTO videojuegos (titulo, descripcion, categoria_id) VALUES (?, ?, ?)")
            ->execute([$titulo, $descripcion, $categoriaId]);
        $newId = (int)$pdo->lastInsertId();

        $stmt = $pdo->prepare("
            SELECT v.id, v.titulo, v.descripcion, c.id AS categoria_id, c.id AS categoriaId,
                   c.nombre_categoria, c.nombre_categoria AS nombreCategoria
            FROM videojuegos v JOIN categories c ON v.categoria_id = c.id
            WHERE v.id = ?
        ");
        $stmt->execute([$newId]);
        jsonResponse($stmt->fetch(), 201);
    }

    // PUT /api/games/{id}
    if ($id !== null && $method === 'PUT') {
        requireAuth();
        $body        = getRequestBody();
        $titulo      = trim($body['titulo']      ?? '');
        $descripcion = trim($body['descripcion'] ?? '');

        if (!$titulo) jsonError('El título es requerido.');

        $stmt = $pdo->prepare("SELECT id FROM videojuegos WHERE id = ?");
        $stmt->execute([$id]);
        if (!$stmt->fetch()) jsonError('Juego no encontrado.', 404);

        $pdo->prepare("UPDATE videojuegos SET titulo = ?, descripcion = ? WHERE id = ?")
            ->execute([$titulo, $descripcion, $id]);

        $stmt = $pdo->prepare("
            SELECT v.id, v.titulo, v.descripcion, c.id AS categoria_id, c.id AS categoriaId,
                   c.nombre_categoria, c.nombre_categoria AS nombreCategoria
            FROM videojuegos v JOIN categories c ON v.categoria_id = c.id
            WHERE v.id = ?
        ");
        $stmt->execute([$id]);
        jsonResponse($stmt->fetch());
    }

    // DELETE /api/games/{id}
    if ($id !== null && $method === 'DELETE') {
        requireAuth();
        $stmt = $pdo->prepare("SELECT id FROM videojuegos WHERE id = ?");
        $stmt->execute([$id]);
        if (!$stmt->fetch()) jsonError('Juego no encontrado.', 404);

        $pdo->prepare("DELETE FROM videojuegos WHERE id = ?")->execute([$id]);
        jsonResponse(null, 204);
    }

    jsonError('Método o ruta no permitida.', 405);
}
