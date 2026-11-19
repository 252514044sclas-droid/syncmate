@echo off
chcp 65001 >nul
cd /d "%~dp0"
title SyncMate AI Backend Launcher

echo =================================================================
echo   SyncMate In-House AI Backend Services
echo =================================================================
echo.

:: 1. Check Python
where python >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Python was not found in your PATH.
    pause
    exit /b 1
)

:: 2. Check PHP
set PHP_BIN=php
where php >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\xampp\php\php.exe" (
        set PHP_BIN=C:\xampp\php\php.exe
    ) else (
        echo [WARNING] PHP not found in PATH or C:\xampp\php\php.exe.
    )
)

:: 3. Launch Python ML microservice (port 5000) in a new window
:: Use full absolute path so it always finds app.py regardless of CWD
start "SyncMate Python AI (:5000)" cmd /k "cd /d "%~dp0backend\ml" && python app.py"

:: 4. Launch PHP REST API server (port 8000) in a new window
start "SyncMate PHP Server (:8000)" cmd /k "%PHP_BIN% -S 0.0.0.0:8000 -t "%~dp0backend""

echo.
echo =================================================================
echo   [OK] Both SyncMate backend services are starting...
echo.
echo   Python AI Engine : http://127.0.0.1:5000
echo   PHP REST API     : http://127.0.0.1:8000/api/
echo   Android Emulator : http://10.0.2.2:8000/api/
echo =================================================================
echo.
echo Keep those two windows open while using the Android app.
echo You may close this launcher window now.
echo.
pause
