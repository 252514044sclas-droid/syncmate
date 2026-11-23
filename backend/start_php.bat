@echo off
title SyncMate PHP Server (:8000)
cd /d "%~dp0"
set PHP_BIN=php
where php >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\xampp\php\php.exe" (
        set PHP_BIN=C:\xampp\php\php.exe
    )
)
echo [*] Starting PHP REST API server on port 8000...
"%PHP_BIN%" -S 0.0.0.0:8000 router.php
if %ERRORLEVEL% neq 0 (
    echo [ERROR] PHP server exited with code %ERRORLEVEL%.
    pause
)
