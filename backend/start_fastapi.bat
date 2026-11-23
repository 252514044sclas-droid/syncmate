@echo off
chcp 65001 >nul
cd /d "%~dp0\.."
title SyncMate FastAPI AI Backend (:8000)
echo [*] Starting SyncMate FastAPI AI Backend on 0.0.0.0:8000...
python -m uvicorn main:app --host 0.0.0.0 --port 8000
if %ERRORLEVEL% neq 0 (
    echo [!] Server exited with error.
    pause
)
