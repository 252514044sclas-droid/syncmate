# 🏠 SyncMate — AI-Powered Student Housing & Sublease Platform

<p align="center">
  <img src="https://img.shields.io/badge/Language-Kotlin%202.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-00C853?style=for-the-badge&logo=android&logoColor=white" alt="MVVM" />
  <img src="https://img.shields.io/badge/Backend-FastAPI%20Python-009688?style=for-the-badge&logo=fastapi&logoColor=white" alt="FastAPI" />
  <img src="https://img.shields.io/badge/Cloud-Firebase%20Auth%20%7C%20Firestore-FFA000?style=for-the-badge&logo=firebase&logoColor=white" alt="Firebase" />
  <img src="https://img.shields.io/badge/Platform-Android%20API%2026%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
</p>

---

## 📌 Overview

**SyncMate** is a production-grade native Android mobile application and dedicated Python AI backend microservice engineered for university students. It solves student sublet discovery, roommate lifestyle matching, and lease contract fraud prevention with deep algorithmic calibration for Indian university hubs (Chennai, Bengaluru, Pune, Delhi NCR, Mumbai, Hyderabad, and Coimbatore).

---

## 🏛️ System Architecture

SyncMate combines clean architecture on Android with single-source-of-truth real-time cloud data and in-house machine learning microservices:

```text
               ┌────────────────────────────────────────────────────────┐
               │              SyncMate Android App                      │
               │  • Jetpack Compose & Material 3 (Adaptive Theme)       │
               │  • StateFlow + Coroutines + MVVM Architecture          │
               │  • Room Database (Offline-First Housing Cache)         │
               └──────────┬─────────────────────────────┬───────────────┘
                          │                             │
          Real-Time Sync  │                             │  Authenticated AI API
          & Auth Flow     │                             │  (Bearer Token / Rate-Limited)
                          ▼                             ▼
       ┌──────────────────────────────┐     ┌───────────────────────────────────┐
       │   Firebase Cloud Platform    │     │   FastAPI AI Microservice (8000)  │
       │                              │     │                                   │
       │ • Firebase Authentication    │     │ • Module 1: Fair-Rent Predictor   │
       │   - Verified university domain│     │   (Calibrated 2026 Indian Market) │
       │   - Strong password validation│     │ • Module 2: Contract Guard AI     │
       │ • Cloud Firestore            │     │   (TF-IDF + Calibrated Classifier)│
       │   - users, requests, chats   │     │ • Module 3: Scam Shield NLP       │
       │   - Strict security rules    │     │ • Module 4: Neural Roommate Match │
       │ • Firebase Cloud Storage     │     │ • In-House Student Assistant Chat │
       │   - Property photo uploads   │     │ • SlowAPI Rate Limiter            │
       │ • Firebase Cloud Messaging   │     │ • 10MB Payload Size Validator     │
       │   - FCM Push notifications   │     └───────────────────────────────────┘
       └──────────────────────────────┘
```

---

## 🚀 Key Modules & Capabilities

### 1. Real Firebase Authentication & Verified Student Status
- **Authentication**: `FirebaseAuth` handles sign-in, registration, password reset, and email verification.
- **University Domain Allow-List**: Strict allow-list of premier Indian university domains (`iitm.ac.in`, `annauniv.edu`, `srmist.edu.in`, `vit.ac.in`, `du.ac.in`, `bits-pilani.ac.in`, etc.) including subdomains and `.edu` / `.ac.in` / `.edu.in` TLDs.
- **Password Strength Policy**: Minimum 8 characters with at least one uppercase, one lowercase, one digit, and one special symbol.
- **Account Control**: In-app "Delete Account Permanently" flow cleans up user data and deletes the Firebase account.

### 2. Firestore Single Source of Truth
- **Roommate Matching & Requests**: Managed via `SyncMateFirestore` with composite real-time listeners.
- **1-on-1 Student Chat**: Real-time message synchronization with subcollection structure (`chats/{chatId}/messages`).
- **Strict Firestore Security Rules** (`firestore.rules`):
  - Private contact fields (`email`, `phone`) stored in owner-only subcollections (`users/{uid}/private/contact`).
  - Strict participant checks on conversations and messages (max 2,000 characters).
  - Safe Block & Report moderation for student safety.

### 3. In-House AI Models (`ml/`)
- **Fair-Rent Predictor** (`ml/fair_price_model.pkl`): Calibrated to realistic 2026 Indian rental rates across 25+ Chennai zones and major metropolitan IT corridors.
- **Contract Guard AI** (`ml/contract_guard_model.pkl`): Self-contained TF-IDF + Classifier trained to flag 10 Indian rental scam traps (unilateral rent hikes, non-refundable deposit traps, 24-hour lockout clauses, upfront wire demands).
- **Roommate Neural Matcher** (`ml/roommate_matcher.pkl`): Evaluates 8 lifestyle dimensions (sleep rhythms, cleanliness, quiet study hours, dietary preferences, budget alignment).
- **AI Consent & Disclosure**: Android displays clear privacy disclosures and requests explicit user consent before submitting documents or rental details to AI inference pipelines.

### 4. Offline-First Housing Listings
- **Room Database Cache**: `HousingDao` provides immediate zero-latency startup and instant offline browsing of verified sublets.
- **Automatic Background Sync**: `NetworkHousingRepositoryImpl` seeds listings on first run, refreshes from the network in the background, and persists changes locally.
- **Photo Uploads**: Direct camera/gallery integration with Firebase Cloud Storage (`listing_photos/{uuid}.jpg`).

---

## 🛠️ Project Setup & Installation

### Prerequisites
- **Android Studio** (Ladybug or newer) with Android SDK 34 / 35.
- **JDK 17 or JDK 21**.
- **Python 3.10+**.
- A **Firebase Project** with Authentication, Firestore Database, and Cloud Storage enabled.

### 1. Firebase Configuration
1. In the [Firebase Console](https://console.firebase.google.com/), create an Android app with package name `com.example.syncmate`.
2. Download `google-services.json` and place it in the `app/` directory (`app/google-services.json`).
   - A template is provided at `app/google-services.json.example`.
3. Enable **Email/Password** authentication under *Authentication > Sign-in method*.
4. Deploy Firestore security rules:
   ```bash
   firebase deploy --only firestore:rules,firestore:indexes
   ```

### 2. Python AI Backend Microservice
1. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```
2. Configure environment variables (copy `.env.example` to `.env`):
   ```bash
   cp .env.example .env
   ```
   Key environment variables:
   - `PORT=8000`
   - `FIREBASE_AUTH_ENABLED=true`
   - `MAX_UPLOAD_SIZE_MB=10`
   - `RATE_LIMIT=60/minute`
   - `FIREBASE_CREDENTIALS_PATH=firebase-credentials.json` (optional for local token verification fallback)

3. Start the FastAPI backend server:
   ```bash
   python -m uvicorn main:app --host 0.0.0.0 --port 8000
   ```
   Or on Windows:
   ```bat
   start_backend.bat
   ```

4. Verify server health:
   ```bash
   curl http://localhost:8000/health
   ```

### 3. Android Application
1. Configure backend URL in `local.properties` (optional, defaults to `10.0.2.2:8000` for Android emulator):
   ```properties
   SYNC_MATE_BASE_URL="http://10.0.2.2:8000/"
   ```
2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   # On Windows:
   .\gradlew.bat assembleDebug
   ```
3. Run on connected device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing

### Backend Integration Tests (Pytest)
Run the automated backend test suite covering health status, rate limiting, 410 legacy deprecation, 413 file size limits, and authenticated AI inference:
```bash
python -m pytest tests/test_api.py -v
```

### Android Unit Tests (JUnit)
Run the Kotlin unit test suite covering password strength rules, university domain matching, and DTO response parsing:
```bash
./gradlew test
# On Windows:
.\gradlew.bat test
```

---

## 🛡️ Security & Privacy Guardrails

- **Encrypted Storage**: Sensitive local session preferences are encrypted using `EncryptedSharedPreferences` with automatic fallback.
- **Network Security Configuration**: Cleartext HTTP traffic is disabled globally; debug builds only permit loopback connections (`10.0.2.2`, `localhost`).
- **Rate Limiting**: AI endpoints enforce `slowapi` rate limits (30–60 requests/min per IP).
- **ProGuard / R8**: Minification and obfuscation configured in `app/proguard-rules.pro`.
- **Zero Third-Party Vendor Lock-in**: All ML models run locally in-process with zero external paid API dependencies.

---

## 📄 License
This project is licensed under the Apache License 2.0.
