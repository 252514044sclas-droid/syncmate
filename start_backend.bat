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
    echo [ERROR] Python was not found in your PATH!
    pause
    exit /b 1
)

:: 2. Check PHP
set PHP_BIN=php
where php >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if not exist "C:\xampp\php\php.exe" (
        echo [WARNING] PHP was not found in PATH or C:\xampp\php\php.exe.
    )
)

:: 3. Check ADB for automatic port forwarding
set ADB_BIN=adb
where adb >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
        set ADB_BIN="%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    )
)

:: Automatically forward ports if emulator/device is connected
echo [*] Configuring ADB port forwarding (bypasses all firewall blocks)...
%ADB_BIN% reverse tcp:8000 tcp:8000 >nul 2>&1
%ADB_BIN% reverse tcp:5000 tcp:5000 >nul 2>&1

echo [*] Launching SyncMate FastAPI AI Server (port 8000, 0.0.0.0)...
start "SyncMate FastAPI AI (:8000)" "%~dp0backend\start_fastapi.bat"

echo.
echo =================================================================
echo   [OK] SyncMate FastAPI AI Backend is starting!
echo   - Local PC:          http://127.0.0.1:8000
echo   - Physical Device:   http://172.23.176.202:8000
echo   - Android Emulator:  http://10.0.2.2:8000
echo   - Listening on:      0.0.0.0:8000 (all network interfaces)
echo =================================================================
echo Keep the two new windows open while running the Android app.
echo You can close this launcher window now.
echo.
pause
