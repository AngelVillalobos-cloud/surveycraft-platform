<?php
// ============================================================
//  SurveyCraft - Verificador e Importador de Base de Datos
//  Este script es ejecutado por iniciar_surveycraft.bat
// ============================================================

require_once __DIR__ . '/api/config/database.php';

echo "\n[1/3] Conectando a MySQL en " . DB_HOST . ":" . DB_PORT . " con usuario '" . DB_USER . "'...\n";

try {
    // 1. Conexión al servidor MySQL general (sin especificar DB)
    $dsnServer = "mysql:host=" . DB_HOST . ";port=" . DB_PORT . ";charset=utf8mb4";
    $pdoServer = new PDO($dsnServer, DB_USER, DB_PASS, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
    ]);
    echo "  -> Conexión con el servidor MySQL exitosa.\n";

    // 2. Verificar o crear la base de datos
    echo "\n[2/3] Verificando base de datos '" . DB_NAME . "'...\n";
    $pdoServer->exec("CREATE DATABASE IF NOT EXISTS `" . DB_NAME . "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
    echo "  -> Base de datos '" . DB_NAME . "' verificada o creada correctamente.\n";

    // 3. Conectar a la base de datos e importar schema.sql si falta alguna tabla
    $dsnDB = "mysql:host=" . DB_HOST . ";port=" . DB_PORT . ";dbname=" . DB_NAME . ";charset=utf8mb4";
    $pdoDB = new PDO($dsnDB, DB_USER, DB_PASS, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
    ]);

    $tables = $pdoDB->query("SHOW TABLES")->fetchAll(PDO::FETCH_COLUMN);
    $requiredTables = ['roless', 'usuarios', 'categories', 'videojuegos', 'surveys', 'questions', 'options', 'responses'];

    $missing = array_diff($requiredTables, $tables);

    if (count($missing) > 0) {
        echo "\n[3/3] Importando tablas desde schema.sql (Faltaban: " . implode(', ', $missing) . ")...\n";
        $sqlFile = __DIR__ . '/schema.sql';
        if (!file_exists($sqlFile)) {
            throw new Exception("No se encontró el archivo schema.sql en " . $sqlFile);
        }
        $sql = file_get_contents($sqlFile);
        $pdoDB->exec($sql);
        echo "  -> ¡Tablas y roles importados con éxito desde schema.sql!\n";
    } else {
        echo "\n[3/3] Todas las tablas requeridas ya existen (" . implode(', ', $tables) . ").\n";
    }

    echo "\n======================================================\n";
    echo " [OK] SURVEYCRAFT ESTÁ LISTO PARA USARSE\n";
    echo "======================================================\n\n";
    exit(0);

} catch (PDOException $e) {
    echo "\n[ERROR DE CONEXION]: " . $e->getMessage() . "\n";
    echo "\nCONSEJO:\n";
    echo "Revisa el archivo 'api/config/database.php'.\n";
    echo "Verifica que el puerto (" . DB_PORT . ") y la contrasena ('" . DB_PASS . "') coincidan con tu MySQL.\n\n";
    exit(1);
} catch (Exception $e) {
    echo "\n[ERROR]: " . $e->getMessage() . "\n\n";
    exit(1);
}
