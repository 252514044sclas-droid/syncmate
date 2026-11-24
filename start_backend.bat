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

:: 2. Check ADB for automatic port forwarding
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

echo [*] Launching SyncMate FastAPI AI Server (port 8000, 0.0.0.0)...
python -m uvicorn main:app --host 0.0.0.0 --port 8000

pause
