@echo off
title SyncMate Python AI (:5000)
cd /d "%~dp0ml"
echo [*] Starting Python ML Microservice on port 5000...
python app.py
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Python ML service exited with code %ERRORLEVEL%.
    pause
)
