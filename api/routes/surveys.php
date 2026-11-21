<?php
// ============================================================
//  SurveyCraft - Rutas de Encuestas (Surveys)
//  GET    /api/surveys
//  GET    /api/surveys/my-surveys
//  GET    /api/surveys/tipo/{tipo}
//  GET    /api/surveys/{id}
//  GET    /api/surveys/{id}/results
//  POST   /api/surveys
//  PUT    /api/surveys/{id}
//  DELETE /api/surveys/{id}
//  POST   /api/surveys/{id}/respond
//  PATCH  /api/surveys/{id}/status
// ============================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../middleware/auth.php';

// -------------------------------------------------------
// Helper: construir DTO completo de una encuesta con preguntas, opciones y votos
// -------------------------------------------------------
function buildSurveyDTO(PDO $pdo, int $surveyId, ?int $userId = null): ?array {
    $stmt = $pdo->prepare("
        SELECT s.*, u.username AS crafter_username, v.titulo AS juego_titulo, c.nombre_categoria
        FROM surveys s
        LEFT JOIN usuarios u ON s.crafter_id = u.user_id
        LEFT JOIN videojuegos v ON s.juego_id = v.id
        LEFT JOIN categories c ON v.categoria_id = c.id
        WHERE s.survey_id = ?
    ");
    $stmt->execute([$surveyId]);
    $survey = $stmt->fetch();
    if (!$survey) return null;

    // Verificar si este usuario específico ya respondió
    $yaRespondida = false;
    $respuestasUsuario = [];
    if ($userId !== null) {
        $chk = $pdo->prepare("
            SELECT r.question_id, r.option_id, o.opcion_texto
            FROM responses r
            JOIN questions q ON r.question_id = q.question_id
            JOIN options o ON r.option_id = o.option_id
            WHERE q.survey_id = ? AND r.player_id = ?
        ");
        $chk->execute([$surveyId, $userId]);
        $uAnswers = $chk->fetchAll();
        if (count($uAnswers) > 0) {
            $yaRespondida = true;
            foreach ($uAnswers as $ua) {
                $respuestasUsuario[(int)$ua['question_id']] = (int)$ua['option_id'];
            }
        }
    }

    // Preguntas
    $qStmt = $pdo->prepare("SELECT * FROM questions WHERE survey_id = ? ORDER BY question_id");
    $qStmt->execute([$surveyId]);
    $questions = $qStmt->fetchAll();

    $totalRespuestas = 0;
    $preguntasDTO = [];

    foreach ($questions as $q) {
        $oStmt = $pdo->prepare("SELECT * FROM options WHERE question_id = ? ORDER BY option_id");
        $oStmt->execute([$q['question_id']]);
        $options = $oStmt->fetchAll();

        $votosPregunta = 0;
        $opcionesDTO   = [];

        foreach ($options as $o) {
            $vStmt = $pdo->prepare("SELECT COUNT(*) AS cnt FROM responses WHERE option_id = ?");
            $vStmt->execute([$o['option_id']]);
            $votos = (int)$vStmt->fetch()['cnt'];
            $votosPregunta += $votos;

            $opcionesDTO[] = [
                'optionId'    => (int)$o['option_id'],
                'textoOpcion' => $o['opcion_texto'],
                'votos'       => $votos,
                'porcentaje'  => 0, // se calcula abajo
                'miVoto'      => isset($respuestasUsuario[(int)$q['question_id']]) && $respuestasUsuario[(int)$q['question_id']] === (int)$o['option_id'],
            ];
        }

        // Calcular porcentajes
        foreach ($opcionesDTO as &$oDto) {
            $oDto['porcentaje'] = $votosPregunta > 0
                ? round($oDto['votos'] / $votosPregunta * 100, 1)
                : 0;
        }
        unset($oDto);

        if ($totalRespuestas === 0) $totalRespuestas = $votosPregunta;

        $preguntasDTO[] = [
            'questionId'    => (int)$q['question_id'],
            'textoPregunta' => $q['pregunta_texto'],
            'tipoPregunta'  => $q['tipo_pregunta'],
            'opciones'      => $opcionesDTO,
        ];
    }

    return [
        'surveyId'         => (int)$survey['survey_id'],
        'titulo'           => $survey['titulo'],
        'descripcion'      => $survey['descripcion'],
        'estado'           => $survey['estado'],
        'juegoId'          => $survey['juego_id'] !== null ? (int)$survey['juego_id'] : null,
        'juegoTitulo'      => $survey['juego_titulo'] ?? null,
        'nombreCategoria'  => $survey['nombre_categoria'] ?? null,
        'tipoEncuesta'     => $survey['tipo_encuesta'],
        'crafterId'        => $survey['crafter_id'] !== null ? (int)$survey['crafter_id'] : null,
        'crafterUsername'  => !empty($survey['crafter_username']) ? $survey['crafter_username'] : 'Anónimo',
        'totalRespuestas'  => $totalRespuestas,
        'yaRespondida'     => $yaRespondida,
        'respuestasUsuario'=> $respuestasUsuario,
        'preguntas'        => $preguntasDTO,
    ];
}

// -------------------------------------------------------
// Router principal de /api/surveys
// -------------------------------------------------------
function handleSurveys(array $segments): void {
    $pdo    = getDB();
    $method = getMethod();

    // /api/surveys
    if (count($segments) === 0) {
        // GET  → listar todas
        if ($method === 'GET') {
            $user   = getAuthUser();
            $userId = $user ? (int)$user['user_id'] : null;

            $rows = $pdo->query("SELECT survey_id FROM surveys ORDER BY survey_id DESC")->fetchAll();
            $result = [];
            foreach ($rows as $row) {
                $dto = buildSurveyDTO($pdo, (int)$row['survey_id'], $userId);
                if ($dto) $result[] = $dto;
            }
            jsonResponse($result);
        }

        // POST → crear encuesta (solo CRAFTER)
        if ($method === 'POST') {
            $user = requireRole('ROLE_CRAFTER');
            $body = getRequestBody();

            $titulo      = trim($body['titulo']      ?? '');
            $descripcion = trim($body['descripcion'] ?? '');
            $juegoId     = !empty($body['juegoId'])    ? (int)$body['juegoId']     : null;
            $tipo        = trim($body['tipoEncuesta'] ?? '');
            $preguntas   = $body['preguntas']          ?? [];

            if (!$titulo) jsonError('El título es requerido.');

            $pdo->prepare("
                INSERT INTO surveys (titulo, descripcion, estado, juego_id, tipo_encuesta, crafter_id)
                VALUES (?, ?, 'activa', ?, ?, ?)
            ")->execute([$titulo, $descripcion, $juegoId, $tipo, $user['user_id']]);

            $surveyId = (int)$pdo->lastInsertId();

            // Guardar preguntas y opciones
            foreach ($preguntas as $pReq) {
                $pdo->prepare("
                    INSERT INTO questions (pregunta_texto, tipo_pregunta, survey_id)
                    VALUES (?, ?, ?)
                ")->execute([
                    $pReq['texto']        ?? '',
                    $pReq['tipoPregunta'] ?? 'SINGLE_CHOICE',
                    $surveyId,
                ]);
                $qId = (int)$pdo->lastInsertId();

                foreach ($pReq['opciones'] ?? [] as $opText) {
                    $pdo->prepare("INSERT INTO options (opcion_texto, question_id) VALUES (?, ?)")
                        ->execute([$opText, $qId]);
                }
            }

            jsonResponse(buildSurveyDTO($pdo, $surveyId), 201);
        }

        jsonError('Método no permitido.', 405);
    }

    // /api/surveys/my-surveys
    if ($segments[0] === 'my-surveys' && count($segments) === 1) {
        if ($method !== 'GET') jsonError('Método no permitido.', 405);
        $user = requireAuth();
        $rows = $pdo->prepare("SELECT survey_id FROM surveys WHERE crafter_id = ? ORDER BY survey_id DESC");
        $rows->execute([$user['user_id']]);
        $result = [];
        foreach ($rows->fetchAll() as $row) {
            $dto = buildSurveyDTO($pdo, (int)$row['survey_id'], (int)$user['user_id']);
            if ($dto) $result[] = $dto;
        }
        jsonResponse($result);
    }

    // /api/surveys/answered  (Encuestas que este usuario ya respondió)
    if ($segments[0] === 'answered' && count($segments) === 1) {
        if ($method !== 'GET') jsonError('Método no permitido.', 405);
        $user = requireAuth();
        $stmt = $pdo->prepare("
            SELECT DISTINCT q.survey_id
            FROM responses r
            JOIN questions q ON r.question_id = q.question_id
            WHERE r.player_id = ?
            ORDER BY q.survey_id DESC
        ");
        $stmt->execute([$user['user_id']]);
        $rows = $stmt->fetchAll();
        $result = [];
        foreach ($rows as $row) {
            $dto = buildSurveyDTO($pdo, (int)$row['survey_id'], (int)$user['user_id']);
            if ($dto) $result[] = $dto;
        }
        jsonResponse($result);
    }

    // /api/surveys/tipo/{tipoEncuesta}
    if ($segments[0] === 'tipo' && isset($segments[1])) {
        if ($method !== 'GET') jsonError('Método no permitido.', 405);
        $tipo = $segments[1];
        $stmt = $pdo->prepare("SELECT survey_id, titulo, descripcion, juego_id, estado FROM surveys WHERE tipo_encuesta = ?");
        $stmt->execute([$tipo]);
        $result = [];
        foreach ($stmt->fetchAll() as $s) {
            $result[] = [
                'surveyId'    => (int)$s['survey_id'],
                'titulo'      => $s['titulo'],
                'descripcion' => $s['descripcion'],
                'juegoId'     => $s['juego_id'] !== null ? (int)$s['juego_id'] : null,
                'estado'      => $s['estado'],
            ];
        }
        jsonResponse($result);
    }

    // /api/surveys/{id}  o  /api/surveys/{id}/results  o  /api/surveys/{id}/respond  o  /api/surveys/{id}/status
    if (is_numeric($segments[0])) {
        $surveyId  = (int)$segments[0];
        $subRoute  = $segments[1] ?? null;

        // GET /api/surveys/{id}  o  /api/surveys/{id}/results
        if ($method === 'GET' && ($subRoute === null || $subRoute === 'results')) {
            $user   = getAuthUser();
            $userId = $user ? (int)$user['user_id'] : null;
            $dto    = buildSurveyDTO($pdo, $surveyId, $userId);
            if (!$dto) jsonError('Encuesta no encontrada.', 404);
            jsonResponse($dto);
        }

        // PUT /api/surveys/{id}
        if ($method === 'PUT' && $subRoute === null) {
            $user = requireAuth();
            $check = $pdo->prepare("SELECT crafter_id FROM surveys WHERE survey_id = ?");
            $check->execute([$surveyId]);
            $survey = $check->fetch();
            if (!$survey) jsonError('Encuesta no encontrada.', 404);
            if ((int)$survey['crafter_id'] !== (int)$user['user_id']) jsonError('Sin permiso.', 403);

            $body        = getRequestBody();
            $titulo      = trim($body['titulo']      ?? '');
            $descripcion = trim($body['descripcion'] ?? '');
            $juegoId     = !empty($body['juegoId'])  ? (int)$body['juegoId']     : null;
            $tipo        = trim($body['tipoEncuesta'] ?? '');

            $pdo->prepare("UPDATE surveys SET titulo=?, descripcion=?, juego_id=?, tipo_encuesta=? WHERE survey_id=?")
                ->execute([$titulo, $descripcion, $juegoId, $tipo, $surveyId]);

            jsonResponse(buildSurveyDTO($pdo, $surveyId, (int)$user['user_id']));
        }

        // DELETE /api/surveys/{id}
        if ($method === 'DELETE' && $subRoute === null) {
            $user = requireAuth();
            $check = $pdo->prepare("SELECT crafter_id FROM surveys WHERE survey_id = ?");
            $check->execute([$surveyId]);
            $survey = $check->fetch();
            if (!$survey) jsonError('Encuesta no encontrada.', 404);
            if ((int)$survey['crafter_id'] !== (int)$user['user_id']) jsonError('Sin permiso.', 403);

            $pdo->prepare("DELETE FROM surveys WHERE survey_id = ?")->execute([$surveyId]);
            jsonResponse(null, 204);
        }

        // POST /api/surveys/{id}/respond
        if ($method === 'POST' && $subRoute === 'respond') {
            $user = requireAuth();
            $check = $pdo->prepare("SELECT survey_id FROM surveys WHERE survey_id = ?");
            $check->execute([$surveyId]);
            if (!$check->fetch()) jsonError('Encuesta no encontrada.', 404);

            // Validar si el usuario ya respondió anteriormente
            $chkResp = $pdo->prepare("
                SELECT COUNT(*) AS cnt
                FROM responses r
                JOIN questions q ON r.question_id = q.question_id
                WHERE q.survey_id = ? AND r.player_id = ?
            ");
            $chkResp->execute([$surveyId, $user['user_id']]);
            if ((int)$chkResp->fetch()['cnt'] > 0) {
                jsonError('Ya has respondido esta encuesta anteriormente. No es posible responder más de una vez.', 409);
            }

            $body       = getRequestBody();
            $respuestas = $body['respuestas']       ?? [];
            $tiempo     = $body['tiempoJugadoJuego'] ?? null;
            $plataforma = $body['plataformaUsada']   ?? null;
            $sessionId  = $body['sessionId']          ?? bin2hex(random_bytes(16));

            if (empty($respuestas)) {
                jsonError('Debes responder al menos una pregunta.', 400);
            }

            foreach ($respuestas as $answer) {
                $pdo->prepare("
                    INSERT INTO responses (player_id, question_id, option_id, tiempo_jugado, plataforma_usada, submitted_at, session_id)
                    VALUES (?, ?, ?, ?, ?, NOW(), ?)
                ")->execute([
                    $user['user_id'],
                    $answer['questionId'],
                    $answer['optionId'],
                    $tiempo,
                    $plataforma,
                    $sessionId,
                ]);
            }

            jsonResponse(['message' => 'Respuesta registrada con éxito'], 201);
        }

        // PATCH /api/surveys/{id}/status
        if ($method === 'PATCH' && $subRoute === 'status') {
            $user = requireAuth();
            $check = $pdo->prepare("SELECT crafter_id FROM surveys WHERE survey_id = ?");
            $check->execute([$surveyId]);
            $survey = $check->fetch();
            if (!$survey) jsonError('Encuesta no encontrada.', 404);
            if ((int)$survey['crafter_id'] !== (int)$user['user_id']) jsonError('Sin permiso.', 403);

            $status = $_GET['status'] ?? (getRequestBody()['status'] ?? '');
            if (!$status) jsonError('El parámetro status es requerido.');

            $pdo->prepare("UPDATE surveys SET estado = ? WHERE survey_id = ?")->execute([$status, $surveyId]);
            jsonResponse(buildSurveyDTO($pdo, $surveyId));
        }
    }

    jsonError('Ruta de surveys no encontrada.', 404);
}
