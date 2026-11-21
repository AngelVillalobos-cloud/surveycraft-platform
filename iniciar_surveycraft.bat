@echo off
:: ============================================================================
::  SurveyCraft - Lanzador y Verificador Automático
::  Este script enciende los servidores (WAMP/Apache/MySQL), comprueba e
::  importa la base de datos de schema.sql si es necesario, y abre la app.
:: ============================================================================

:: Configurar la consola en UTF-8 para mostrar tildes y caracteres especiales
chcp 65001 >nul
title SurveyCraft - Lanzador del Sistema
color 0B

echo ============================================================================
echo                      🎮 SURVEYCRAFT - LANZADOR 🎮
echo ============================================================================
echo.

:: ----------------------------------------------------------------------------
:: PASO 1: Verificar si WAMP está instalado y ejecutándose
:: ----------------------------------------------------------------------------
echo [1/4] Comprobando servicios de WampServer...

:: Comprobar si el proceso wampmanager.exe está activo
tasklist /FI "IMAGENAME eq wampmanager.exe" 2>NUL | find /I /N "wampmanager.exe" >NUL
if "%ERRORLEVEL%"=="0" (
    echo   -> WampServer ya se encuentra en ejecución.
) else (
    echo   -> Iniciando WampServer...
    if exist "C:\wamp64\wampmanager.exe" (
        start "" "C:\wamp64\wampmanager.exe"
        echo   -> Se ha lanzado WampServer. Esperando 4 segundos a que inicien los servicios...
        timeout /t 4 /nobreak >nul
    ) else (
        echo   -> [Aviso] No se encontró C:\wamp64\wampmanager.exe. Asegúrate de tener tu servidor web activo.
    )
)

:: Intentar iniciar servicios de Windows de Apache y MySQL de WAMP por si están detenidos
net start wampapache64 >nul 2>&1
net start wampmysqld64 >nul 2>&1

:: ----------------------------------------------------------------------------
:: PASO 2: Buscar el ejecutable de PHP dentro de WAMP
:: ----------------------------------------------------------------------------
echo.
echo [2/4] Buscando interprete PHP en WAMP...

set "PHP_EXE="

:: Buscar primero la versión predeterminada instalada en WAMP
if exist "C:\wamp64\bin\php\php8.2.36\php.exe" (
    set "PHP_EXE=C:\wamp64\bin\php\php8.2.36\php.exe"
) else if exist "C:\wamp64\bin\php\php8.5.13\php.exe" (
    set "PHP_EXE=C:\wamp64\bin\php\php8.5.13\php.exe"
) else (
    :: Búsqueda dinámica de cualquier versión de PHP en C:\wamp64\bin\php\
    for /d %%D in ("C:\wamp64\bin\php\php*") do (
        if exist "%%D\php.exe" set "PHP_EXE=%%D\php.exe"
    )
)

:: Si no se encontró en WAMP, buscar php en el PATH global de Windows
if "%PHP_EXE%"=="" (
    where php >nul 2>&1
    if "%ERRORLEVEL%"=="0" (
        set "PHP_EXE=php"
    )
)

if "%PHP_EXE%"=="" (
    echo   [ERROR] No se encontró el ejecutable php.exe.
    echo   Asegúrate de que WAMP esté instalado en C:\wamp64 o agrega PHP a tus Variables de Entorno.
    pause
    exit /b 1
)

echo   -> PHP localizado en: %PHP_EXE%

:: ----------------------------------------------------------------------------
:: PASO 3: Verificar e Importar la Base de Datos automáticamente
:: ----------------------------------------------------------------------------
echo.
echo [3/4] Verificando e importando la base de datos MySQL (schema.sql)...
"%PHP_EXE%" "%~dp0setup.php"

if errorlevel 1 (
    echo.
    echo ----------------------------------------------------------------------------
    echo [ALERTA] Hubo un problema conectando a MySQL.
    echo Revisa que tu servidor MySQL esté activo y edita 'api/config/database.php'
    echo con la contraseña correcta de tu usuario root de MySQL.
    echo ----------------------------------------------------------------------------
    echo.
    pause
    exit /b 1
)

:: ----------------------------------------------------------------------------
:: PASO 4: Abrir SurveyCraft en el navegador predeterminado
:: ----------------------------------------------------------------------------
echo.
echo [4/4] Abriendo SurveyCraft en tu navegador...
start http://localhost/surveycraft-platform-version-php/

echo.
echo ============================================================================
echo   ¡Todo listo! SurveyCraft se ha iniciado correctamente.
echo   Puedes cerrar esta ventana o dejarla abierta mientras usas la aplicacion.
echo ============================================================================
echo.
pause
