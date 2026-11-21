<?php
// ============================================================
//  SurveyCraft - Configuración de base de datos
//  Edita estos valores con los de tu WAMP/XAMPP
// ============================================================

define('DB_HOST', 'localhost');
define('DB_NAME', 'surveycraft');
define('DB_USER', 'root');
define('DB_PASS', '');          // En WAMP/XAMPP el password de root suele estar vacío
define('DB_PORT', '3306');

function getDB(): PDO {
    static $pdo = null;
    if ($pdo === null) {
        $dsn = "mysql:host=" . DB_HOST . ";port=" . DB_PORT . ";dbname=" . DB_NAME . ";charset=utf8mb4";
        $pdo = new PDO($dsn, DB_USER, DB_PASS, [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ]);
    }
    return $pdo;
}
