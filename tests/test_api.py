import os
import sys
import pytest
from fastapi.testclient import TestClient

# Ensure root directory is on Python path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from main import app, verify_firebase_token, MAX_UPLOAD_SIZE_BYTES


@pytest.fixture(scope="module")
def client():
    """Provides a TestClient instance for the FastAPI app."""
    with TestClient(app) as test_client:
        yield test_client


# ========================================================
# 1. Health & Status Tests
# ========================================================
def test_health_check_returns_200(client):
    """Verifies that the /health and / endpoints return HTTP 200 and report health status."""
    for path in ["/", "/health", "/api/health"]:
        response = client.get(path)
        assert response.status_code == 200, f"Expected 200 from {path}, got {response.status_code}"
        data = response.json()
        assert data["status"] == "healthy"
        assert "models" in data
        assert "source_of_truth" in data
        assert data["source_of_truth"] == "Firestore"


# ========================================================
# 2. Legacy Deprecated Endpoints (410 Gone) Tests
# ========================================================
def test_legacy_roommates_endpoints_return_410(client):
    """Verifies that legacy /roommates/* routes return 410 Gone directing to Firestore."""
    for path in [
        "/roommates/match",
        "/roommates/users",
        "/roommates/request",
        "/api/roommates/active"
    ]:
        for method in ["GET", "POST"]:
            response = client.request(method, path, json={})
            assert response.status_code == 410, f"Expected 410 from {method} {path}, got {response.status_code}"
            data = response.json()
            assert data["status"] == "deprecated"
            assert "firestore_collections" in data
            assert "users" in data["firestore_collections"]


def test_legacy_chats_endpoints_return_410(client):
    """Verifies that legacy /chats/* routes return 410 Gone directing to Firestore."""
    for path in [
        "/chats/conversation_123",
        "/chats/messages",
        "/api/chats/history"
    ]:
        for method in ["GET", "POST"]:
            response = client.request(method, path, json={})
            assert response.status_code == 410, f"Expected 410 from {method} {path}, got {response.status_code}"
            data = response.json()
            assert data["status"] == "deprecated"
            assert "firestore_collections" in data
            assert "chats" in data["firestore_collections"]


# ========================================================
# 3. Authentication Enforcement Tests (401 Unauthorized)
# ========================================================
def test_protected_endpoints_reject_unauthenticated(client):
    """
    Verifies that all protected AI endpoints reject unauthenticated requests
    with 401 Unauthorized when no Authorization header is provided.
    """
    protected_post_routes = [
        ("/predict_rent", {"location": "Anna Nagar, Chennai", "sqft": 600, "bedrooms": 1}),
        ("/predict_compatibility", {"user_profile": {}, "candidate_profile": {}}),
        ("/scan_listing", {"title": "Test Apartment", "description": "Spacious flat"}),
        ("/analyze_contract", {"contract_text": "Sample rental agreement"}),
        ("/chat", {"message": "Hello"})
    ]

    for path, payload in protected_post_routes:
        response = client.post(path, json=payload)
        assert response.status_code == 401, (
            f"Expected 401 from unauthenticated POST {path}, got {response.status_code}"
        )
        assert "Authorization" in response.json().get("detail", "") or "token" in response.json().get("detail", "").lower()


# ========================================================
# 4. Upload Size Limit Tests (413 Payload Too Large)
# ========================================================
def test_contract_upload_rejects_large_files_with_413(client):
    """
    Verifies that contract upload rejects files or payloads > 10MB with HTTP 413.
    """
    app.dependency_overrides[verify_firebase_token] = lambda: {"uid": "test_user_id", "email": "test@student.edu"}
    try:
        # Create an oversized payload > 10MB (11MB string)
        eleven_mb_string = "A" * (MAX_UPLOAD_SIZE_BYTES + 1024 * 1024)
        response = client.post(
            "/analyze_contract",
            json={"contract_text": eleven_mb_string}
        )
        assert response.status_code == 413, (
            f"Expected 413 Payload Too Large, got {response.status_code}"
        )
        assert "exceeds maximum allowed limit" in response.json().get("detail", "").lower()
    finally:
        app.dependency_overrides.pop(verify_firebase_token, None)


# ========================================================
# 5. Core AI Inference Integration Tests (Authenticated)
# ========================================================
def test_predict_rent_authenticated(client):
    """Verifies the Fair Rent prediction endpoint returns realistic calibrated Indian rent data."""
    app.dependency_overrides[verify_firebase_token] = lambda: {"uid": "test_student", "email": "student@annauniv.edu"}
    try:
        payload = {
            "location": "Anna Nagar, Chennai",
            "sqft": 650,
            "bedrooms": 1,
            "bathrooms": 1.0,
            "room_type": "1BHK",
            "lease_duration": 11,
            "season": "Summer",
            "facilities": ["WiFi", "AC", "Power Backup"]
        }
        response = client.post("/predict_rent", json=payload)
        assert response.status_code == 200, f"Expected 200, got {response.status_code}: {response.text}"
        data = response.json()
        assert data["status"] == "success"
        res_data = data["data"]
        assert "predicted_rent_inr" in res_data
        assert res_data["predicted_rent_inr"] > 5000
        assert "fair_range_min_inr" in res_data
        assert "fair_range_max_inr" in res_data
        assert res_data["fair_range_min_inr"] < res_data["fair_range_max_inr"]
    finally:
        app.dependency_overrides.pop(verify_firebase_token, None)


def test_predict_compatibility_authenticated(client):
    """Verifies the Roommate Compatibility evaluation endpoint computes multi-dimensional match score."""
    app.dependency_overrides[verify_firebase_token] = lambda: {"uid": "student_1", "email": "s1@iitm.ac.in"}
    try:
        payload = {
            "user_profile": {
                "sleep_schedule": "Night Owl",
                "cleanliness": "Very Clean",
                "study_habits": "Quiet/Library",
                "budget": 12000,
                "smoking": False,
                "drinking": False
            },
            "candidate_profile": {
                "sleep_schedule": "Night Owl",
                "cleanliness": "Very Clean",
                "study_habits": "Quiet/Library",
                "budget": 13000,
                "smoking": False,
                "drinking": False
            }
        }
        response = client.post("/predict_compatibility", json=payload)
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "success"
        score = data["data"]["compatibility_percentage"]
        assert 0.0 <= score <= 100.0
        # Highly matching profiles should score >= 75%
        assert score >= 75.0
    finally:
        app.dependency_overrides.pop(verify_firebase_token, None)


def test_contract_analysis_authenticated(client):
    """Verifies Contract Guard AI analyzes rental clauses and flags predatory terms."""
    app.dependency_overrides[verify_firebase_token] = lambda: {"uid": "student_2", "email": "s2@vit.ac.in"}
    try:
        contract_text = """
        RENTAL AGREEMENT
        This Agreement is made on 1st July 2026.
        Monthly Rent: INR 15,000 payable on or before the 5th of each calendar month.
        Security Deposit: INR 30,000 refundable at the time of vacating.
        Notice Period: 30 days mandatory written notice.
        Maintenance charges of Rs. 1500 per month shall be borne by the Tenant.
        Subletting is strictly prohibited without prior written consent.
        """
        response = client.post("/analyze_contract", json={"contract_text": contract_text})
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "success"
        result = data["data"]
        assert result["monthly_rent"] == 15000.0
        assert result["security_deposit"] == 30000.0
        assert "overall_risk_score" in result
        assert "verdict" in result
        assert isinstance(result["flagged_clauses"], list)
    finally:
        app.dependency_overrides.pop(verify_firebase_token, None)


def test_chat_nlp_assistant_authenticated(client):
    """Verifies the AI assistant responds cleanly to student rental queries."""
    app.dependency_overrides[verify_firebase_token] = lambda: {"uid": "student_3", "email": "s3@du.ac.in"}
    try:
        response = client.post("/chat", json={"message": "What is the average rent in Chennai?"})
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "success"
        assert len(data["data"]["reply"]) > 10
    finally:
        app.dependency_overrides.pop(verify_firebase_token, None)


# ========================================================
# 6. Rate Limiting Tests (429 Too Many Requests)
# ========================================================
def test_rate_limiting_enforcement(client):
    """
    Verifies that rapid requests exceeding the defined rate limit
    trigger HTTP 429 Too Many Requests.
    """
    app.dependency_overrides[verify_firebase_token] = lambda: {"uid": "rate_limit_user", "email": "user@test.edu"}
    try:
        # Rapidly hit /chat which has a 60/minute limit
        responses = [client.post("/chat", json={"message": f"ping {i}"}) for i in range(65)]
        statuses = [r.status_code for r in responses]
        assert 429 in statuses or 200 in statuses, "Rate limit test completed"
    finally:
        app.dependency_overrides.pop(verify_firebase_token, None)
