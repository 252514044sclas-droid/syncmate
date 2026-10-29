# 🐘 SubletSync PHP REST API & MySQL Backend

This folder contains the ready-to-run PHP REST API scripts and MySQL database schema for SubletSync.

---

## 🚀 How to Run Local Backend Server

1. **Start Local MySQL Database**:
   - Open XAMPP / WAMP / MySQL Server.
   - Import `schema.sql` into MySQL to create database `subletsync_db`.

2. **Start Local PHP Development Server**:
   ```bash
   cd backend
   php -S 127.0.0.1:8000 -t api
   ```
   Or if serving via Apache XAMPP:
   Place `backend/` inside `htdocs/` so it is reachable at `http://localhost/backend/api/`.

3. **Android Emulator Access**:
   The Android app is configured in `RetrofitClient.kt` to call:
   `http://10.0.2.2:8000/api/` (which routes directly to your computer's `localhost:8000`).
