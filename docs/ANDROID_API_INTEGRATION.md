# 📱 SubletSync — Android REST API Integration Plan

This document details the mapping between SubletSync Android application screens, Jetpack Compose ViewModels, Repository contracts, and the PHP REST API + MySQL backend endpoints.

---

## 🌐 Centralized Base URL Configuration

- **Android Emulator Access**: `http://10.0.2.2:8000/api/` (or `http://10.0.2.2:80/subletsync_api/`)
- **Physical Device LAN Access**: `http://<YOUR_COMPUTER_LAN_IP>:8000/api/`
- **Production HTTPS Domain**: `https://api.subletsync.app/v1/`

---

## 📌 Screen-to-API Mapping Matrix

| # | Screen | ViewModel | Required API Endpoint | HTTP Method | Request Body / Query Params | Integration Status |
|---|---|---|---|---|---|---|
| 1 | **Login** | `AuthViewModel` | `/auth/login.php` | `POST` | `{ "email": "...", "password": "..." }` | Integrated with Auth Token Storage |
| 2 | **Create Account** | `AuthViewModel` | `/auth/register.php` | `POST` | `{ "name": "...", "email": "...", "password": "...", "university": "..." }` | Integrated |
| 3 | **Verify Email** | `AuthViewModel` | `/auth/verify_email.php` | `POST` | `{ "email": "...", "code": "..." }` | Integrated |
| 4 | **Forgot Password** | `AuthViewModel` | `/auth/forgot_password.php` | `POST` | `{ "email": "..." }` | Integrated |
| 5 | **Dashboard** | `DashboardViewModel` | `/dashboard/summary.php` | `GET` | Header: `Authorization: Bearer <token>` | Integrated with Fallback Data |
| 6 | **Home Marketplace** | `HomeViewModel` | `/listings/featured.php` | `GET` | Header: `Authorization: Bearer <token>` | Integrated |
| 7 | **Search Housing** | `SearchScreen` | `/listings/search.php` | `GET` | `?q=...&room_type=...&max_price=...` | Integrated |
| 8 | **Listing Details** | `HousingViewModel` | `/listings/detail.php` | `GET` | `?id=lst_1` | Integrated |
| 9 | **Post Sublet** | `HousingViewModel` | `/listings/create.php` | `POST` | Multipart or JSON body with property fields | Integrated |
| 10 | **Save / Favorite** | `HousingViewModel` | `/listings/toggle_save.php` | `POST` | `{ "listing_id": "..." }` | Integrated |
| 11 | **Saved Sublets** | `HousingViewModel` | `/listings/saved.php` | `GET` | Header: `Authorization: Bearer <token>` | Integrated |
| 12 | **Fair Rent Estimator** | `PredictionViewModel` | `/ai/predict_rent.php` | `POST` | `{ "sqft": 800, "bedrooms": 1, "location": "...", "room_type": "..." }` | Integrated |
| 13 | **Fake-Listing Shield**| `FakeListingViewModel`| `/ai/analyze_scam.php` | `POST` | `{ "description": "...", "price": 4000 }` | Integrated |
| 14 | **Contract Guard** | `ContractViewModel` | `/ai/analyze_contract.php` | `POST` | Multipart PDF document upload (`file`) | Integrated |
| 15 | **Report Listing** | `HousingViewModel` | `/listings/report.php` | `POST` | `{ "listing_id": "...", "reason": "...", "comments": "..." }` | Integrated |
| 16 | **Student Profile** | `AuthViewModel` | `/user/profile.php` | `GET` / `PUT` | Header: `Authorization: Bearer <token>` | Integrated |
| 17 | **Notifications** | `NotificationsScreen` | `/notifications/list.php` | `GET` | Header: `Authorization: Bearer <token>` | Integrated |
| 18 | **Direct Chat** | `ChatScreen` | `/chat/messages.php` | `GET` / `POST` | `?recipient_id=...` & `{ "message": "..." }` | Integrated |

---

## 🔒 Authentication & Token Security Architecture

- **Token Storage**: Encrypted / persistent access tokens saved via `AppPreferences`.
- **OkHttp Interceptor**: `AuthInterceptor` automatically attaches `Authorization: Bearer <token>` to protected endpoints.
- **Cleartext Traffic**: Network Security Config enabled in `res/xml/network_security_config.xml` for local HTTP development.
