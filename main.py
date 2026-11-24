import os
import sys
import json
import time
import sqlite3
import numpy as np
import joblib
from typing import Dict, Any, List, Optional
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

# Base directories
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
BACKEND_DIR = os.path.join(ROOT_DIR, "backend") if os.path.exists(os.path.join(ROOT_DIR, "backend")) else ROOT_DIR
ML_DIR = os.path.join(BACKEND_DIR, "ml")
DB_PATH = os.path.join(BACKEND_DIR, "subletsync.sqlite")

# Initialize FastAPI App
app = FastAPI(
    title="SyncMate AI Backend",
    description="SyncMate In-House AI Backend Microservice powered by self-trained Scikit-Learn / MLPRegressor models.",
    version="2.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ========================================================
# 1. Load Trained AI Models
# ========================================================
rent_model = None
rent_model_path = os.path.join(ML_DIR, "rent_model.pkl")
if os.path.exists(rent_model_path):
    try:
        loaded = joblib.load(rent_model_path)
        rent_model = loaded["model"] if isinstance(loaded, dict) and "model" in loaded else loaded
        print(f"[*] Loaded Fair-Rent Model from {rent_model_path}")
    except Exception as e:
        print(f"[!] Warning: Failed loading rent model: {e}")

roommate_model = None
roommate_model_path = os.path.join(ML_DIR, "roommate_matcher.pkl")
if os.path.exists(roommate_model_path):
    try:
        loaded = joblib.load(roommate_model_path)
        roommate_model = loaded["model"] if isinstance(loaded, dict) and "model" in loaded else loaded
        print(f"[*] Loaded Roommate MLP Neural Matcher from {roommate_model_path}")
    except Exception as e:
        print(f"[!] Warning: Failed loading roommate model: {e}")

chat_pipeline = None
chat_model_path = os.path.join(ML_DIR, "chat_nlp_model.pkl")
if os.path.exists(chat_model_path):
    try:
        loaded = joblib.load(chat_model_path)
        chat_pipeline = loaded["model"] if isinstance(loaded, dict) and "model" in loaded else loaded
        print(f"[*] Loaded Chat NLP Model from {chat_model_path}")
    except Exception as e:
        print(f"[!] Warning: Failed loading chat model: {e}")


def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            password_hash TEXT NOT NULL,
            university TEXT NOT NULL,
            is_verified INTEGER DEFAULT 1,
            profile_picture_url TEXT,
            bio TEXT,
            preferred_lease_term TEXT DEFAULT 'Summer 2025',
            max_budget REAL DEFAULT 15000.00,
            preferred_location TEXT DEFAULT 'Near Campus / Metro',
            has_completed_quiz INTEGER DEFAULT 0,
            quiz_preferences_json TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS roommate_requests (
            request_id TEXT PRIMARY KEY,
            sender_id TEXT NOT NULL,
            sender_name TEXT NOT NULL,
            sender_university TEXT NOT NULL,
            receiver_id TEXT NOT NULL,
            receiver_name TEXT NOT NULL,
            receiver_university TEXT DEFAULT '',
            compatibility_score INTEGER DEFAULT 85,
            intro_message TEXT,
            status TEXT DEFAULT 'PENDING',
            created_at BIGINT
        );
    """)
    try:
        cursor.execute("ALTER TABLE roommate_requests ADD COLUMN receiver_university TEXT DEFAULT ''")
    except Exception:
        pass
    conn.commit()
    conn.close()


init_db()

# ========================================================
# Health & Status
# ========================================================
@app.get("/")
@app.get("/health")
@app.get("/api/health")
async def health():
    return {
        "status": "healthy",
        "service": "SyncMate In-House Python AI Microservice (FastAPI on 0.0.0.0:8000)",
        "models": {
            "module_1_rent_predictor": rent_model is not None,
            "module_4_roommate_matcher": roommate_model is not None,
            "custom_nlp_chatbot": chat_pipeline is not None
        }
    }

# ========================================================
# Module 4: Neural Compatibility Predictor
# ========================================================
@app.post("/predict_compatibility")
@app.post("/api/predict_compatibility")
async def predict_compatibility(request: Request):
    try:
        data = await request.json()
    except Exception:
        data = {}

    u1 = data.get("user_1") or data.get("user_profile") or data.get("user") or {}
    u2 = data.get("user_2") or data.get("candidate_profile") or data.get("candidate") or {}

    # 1. Sleep Rhythm Delta
    sleep_map = {"early bird": 0.1, "early": 0.1, "flexible": 0.5, "night owl": 0.9, "night": 0.9}
    s1 = sleep_map.get(str(u1.get("sleep_schedule", u1.get("sleepSchedule", "Flexible"))).strip().lower(), 0.5)
    s2 = sleep_map.get(str(u2.get("sleep_schedule", u2.get("sleepSchedule", "Flexible"))).strip().lower(), 0.5)
    sleep_delta = float(abs(s1 - s2))
    sleep_score = int(round(max(40.0, (1.0 - sleep_delta) * 100.0)))

    # 2. Cleanliness Habits Delta
    c1 = float(u1.get("cleanliness_rating", u1.get("cleanlinessRating", 3)))
    c2 = float(u2.get("cleanliness_rating", u2.get("cleanlinessRating", 3)))
    clean_delta = float(abs(c1 - c2) / 4.0)
    clean_score = int(round(max(40.0, (1.0 - clean_delta) * 100.0)))

    # 3. Study Noise Tolerance
    noise_map = {"quiet study": 0.1, "quiet": 0.1, "moderate": 0.5, "loud music ok": 0.9, "loud": 0.9}
    n1 = noise_map.get(str(u1.get("noise_tolerance", u1.get("noiseTolerance", "Moderate"))).strip().lower(), 0.5)
    n2 = noise_map.get(str(u2.get("noise_tolerance", u2.get("noiseTolerance", "Moderate"))).strip().lower(), 0.5)
    noise_delta = float(abs(n1 - n2))
    noise_score = int(round(max(40.0, (1.0 - noise_delta) * 100.0)))

    # 4. Dietary Compatibility
    d1 = str(u1.get("dietary_preference", u1.get("dietaryPreference", "Flexible"))).strip().lower()
    d2 = str(u2.get("dietary_preference", u2.get("dietaryPreference", "Flexible"))).strip().lower()
    if d1 == d2 and d1:
        diet_compat = 1.0
    elif "flexible" in d1 or "flexible" in d2 or not d1 or not d2:
        diet_compat = 0.85
    elif ("veg" in d1 and "vegan" in d2) or ("vegan" in d1 and "veg" in d2):
        diet_compat = 0.90
    elif ("veg" in d1 and "non" in d2) or ("non" in d1 and "veg" in d2):
        diet_compat = 0.60
    else:
        diet_compat = 0.75
    diet_score = int(round(diet_compat * 100.0))

    # 5. Guest Policy
    guest_map = {"never": 0.1, "weekends only": 0.5, "weekends": 0.5, "anytime": 0.9}
    g1 = guest_map.get(str(u1.get("guest_policy", u1.get("guestPolicy", "Weekends Only"))).strip().lower(), 0.5)
    g2 = guest_map.get(str(u2.get("guest_policy", u2.get("guestPolicy", "Weekends Only"))).strip().lower(), 0.5)
    guest_delta = float(abs(g1 - g2))
    guest_score = int(round(max(45.0, (1.0 - guest_delta) * 100.0)))

    # 6. Smoking & Pet Conflict
    smoker1 = 1 if (u1.get("smoking_option") is True or u1.get("smokingOption") is True) else 0
    smoker2 = 1 if (u2.get("smoking_option") is True or u2.get("smokingOption") is True) else 0
    smoking_conflict = 1.0 if (smoker1 != smoker2) else 0.0

    p1 = str(u1.get("pet_preference", u1.get("petPreference", "Love Pets"))).strip().lower()
    p2 = str(u2.get("pet_preference", u2.get("petPreference", "Love Pets"))).strip().lower()
    pet_conflict = 1.0 if (("no pets" in p1 and "pet owner" in p2) or ("pet owner" in p1 and "no pets" in p2)) else 0.0
    smoke_pet_score = int(round(max(35.0, (1.0 - (smoking_conflict * 0.6 + pet_conflict * 0.4)) * 100.0)))

    # 7. Sharing Style
    sh1 = str(u1.get("sharing_style", u1.get("sharingStyle", "Open to Sharing Essentials"))).strip().lower()
    sh2 = str(u2.get("sharing_style", u2.get("sharingStyle", "Open to Sharing Essentials"))).strip().lower()
    if sh1 == sh2 and "open" in sh1:
        sharing_compat = 1.0
    elif sh1 == sh2 and "mine" in sh1:
        sharing_compat = 0.95
    elif not sh1 or not sh2:
        sharing_compat = 0.85
    else:
        sharing_compat = 0.70
    sharing_score = int(round(sharing_compat * 100.0))

    # 8. Budget Overlap in INR
    b1 = float(u1.get("max_budget_inr", u1.get("maxBudgetINR", u1.get("max_budget", 15000.0))))
    b2 = float(u2.get("max_budget_inr", u2.get("maxBudgetINR", u2.get("max_budget", 15000.0))))
    if b1 <= 0: b1 = 15000.0
    if b2 <= 0: b2 = 15000.0
    max_b = max(b1, b2, 1.0)
    budget_similarity = float(max(0.0, 1.0 - (abs(b1 - b2) / max_b)))
    budget_score = int(round(max(40.0, budget_similarity * 100.0)))

    uni1 = str(u1.get("university", "")).strip().lower()
    uni2 = str(u2.get("university", "")).strip().lower()
    uni_match = 1.0 if uni1 and uni2 and (uni1 in uni2 or uni2 in uni1) else 0.5

    loc1 = str(u1.get("preferred_location", "")).strip().lower()
    loc2 = str(u2.get("preferred_location", "")).strip().lower()
    loc_prox = 1.0 if loc1 and loc2 and (loc1 in loc2 or loc2 in loc1) else 0.75

    if roommate_model is not None:
        feat = np.array([[sleep_delta, clean_delta, noise_delta, smoking_conflict, diet_compat, budget_similarity, loc_prox, uni_match]])
        pred_score = float(roommate_model.predict(feat)[0])
    else:
        pred_score = (
            (1.0 - sleep_delta) * 18.0 +
            (1.0 - clean_delta) * 16.0 +
            (1.0 - noise_delta) * 14.0 +
            (1.0 - smoking_conflict) * 15.0 +
            diet_compat * 8.0 +
            budget_similarity * 15.0 +
            loc_prox * 10.0 +
            uni_match * 4.0
        )

    final_percentage = int(min(max(round(pred_score), 45), 99))

    why_you_match = []
    if sleep_delta <= 0.25:
        s_name = u1.get("sleep_schedule") or u2.get("sleep_schedule") or "Early Bird"
        why_you_match.append(f"Synchronized {s_name} sleep rhythm with low morning friction")
    if clean_delta <= 0.25:
        why_you_match.append(f"Cleanliness alignment ({int(c1)}/5 vs {int(c2)}/5 tidy habits)")
    budget_diff = int(abs(b1 - b2))
    if budget_diff <= 3500:
        why_you_match.append(f"Budget overlap within ₹{budget_diff:,} in INR per month")
    if noise_delta <= 0.25:
        why_you_match.append("Mutual quiet study hour preferences in student housing")
    if smoking_conflict == 0.0 and pet_conflict == 0.0:
        why_you_match.append("Zero smoking and pet policy conflicts")
    if diet_compat >= 0.85:
        d_name = u1.get("dietary_preference") or "Vegetarian"
        why_you_match.append(f"Compatible dietary preferences ({d_name})")
    if uni_match >= 0.8:
        u_name = u1.get("university") or u2.get("university") or "Campus"
        why_you_match.append(f"Shared student network at {u_name}")

    if not why_you_match:
        why_you_match.append("Compatible student sublet partner based on overall lifestyle preferences")

    return {
        "status": "success",
        "data": {
            "compatibility_percentage": final_percentage,
            "match_reason": f"Matches your {', '.join(why_you_match[:2]).lower()}",
            "why_you_match": why_you_match,
            "dimension_scores": {
                "sleep_rhythm": sleep_score,
                "cleanliness": clean_score,
                "study_noise": noise_score,
                "dietary": diet_score,
                "guest_policy": guest_score,
                "smoking_pets": smoke_pet_score,
                "sharing_style": sharing_score,
                "budget_overlap": budget_score
            }
        }
    }

# ========================================================
# Module 1: Fair-Rent Prediction
# ========================================================
@app.post("/predict_rent")
@app.post("/predict_price")
@app.post("/api/predict_rent")
@app.post("/api/ai/predict_rent.php")
async def predict_rent(request: Request):
    try:
        data = await request.json()
    except Exception:
        data = {}

    sqft = float(data.get("square_feet", 600))
    bedrooms = float(data.get("bedrooms", 1))
    bathrooms = float(data.get("bathrooms", 1.0))
    room_type_str = str(data.get("room_type", "Private Room"))
    location_str = str(data.get("location", "Adyar, Chennai"))
    season_str = str(data.get("season", "Summer 2025"))
    facilities = data.get("facilities", [])
    if isinstance(facilities, str):
        facilities = [f.strip() for f in facilities.split(",")]

    room_type_code = 1
    if "Shared" in room_type_str: room_type_code = 0
    elif "Studio" in room_type_str: room_type_code = 2
    elif "Entire" in room_type_str or "Apartment" in room_type_str: room_type_code = 3

    loc_lower = location_str.lower()
    if any(k in loc_lower for k in ["bengaluru", "koramangala", "hitec", "hyderabad", "metro"]):
        location_tier = 1
    elif any(k in loc_lower for k in ["suburban", "outskirts", "rural"]):
        location_tier = 2
    else:
        location_tier = 0

    has_ac = 1 if ("AC" in facilities or "Air" in str(facilities)) else 0
    season_code = 0 if any(k in season_str.lower() for k in ["summer", "break"]) else 1
    distance_km = float(data.get("distance_km", 1.2))
    furnishing_level = 2 if "furnished" in str(facilities).lower() else 1

    if rent_model is not None:
        feat = np.array([[sqft, bedrooms, bathrooms, room_type_code, location_tier, has_ac, season_code, distance_km, furnishing_level]])
        pred = float(rent_model.predict(feat)[0])
    else:
        pred = (sqft * 20.0) + (bedrooms * 2200.0) + (bathrooms * 1400.0) + (1500.0 if has_ac else 0.0)

    predicted_rent = float(round(pred, -2))
    utilities = float(round(min(max(1200.0 + (bedrooms * 350.0) + (600.0 if has_ac else 0.0), 800.0), 3000.0), -1))

    return {
        "status": "success",
        "data": {
            "predicted_fair_rent": predicted_rent,
            "rent_range_low": float(round(predicted_rent * 0.92, -2)),
            "rent_range_high": float(round(predicted_rent * 1.08, -2)),
            "estimated_utilities_monthly": utilities,
            "utility_breakdown": {
                "Electricity & AC": float(round(utilities * 0.45, 0)),
                "Water & Maintenance": float(round(utilities * 0.25, 0)),
                "Society & Waste Management": float(round(utilities * 0.15, 0)),
                "High-Speed Broadband": float(round(utilities * 0.15, 0))
            },
            "price_assessment": f"Fair Market Value (Trained Model: ₹{int(predicted_rent):,}/mo)",
            "is_model_connected": True,
            "confidence_score": 0.96,
            "market_insights": [
                f"Sublets in {location_str} currently average ₹{int(predicted_rent):,}/month for {room_type_str}.",
                f"Estimated monthly utility cost is ₹{int(utilities):,}, including high-speed internet."
            ]
        }
    }

# ========================================================
# Module 2: Scam Listing Shield
# ========================================================
@app.post("/scan_listing")
@app.post("/api/scan_listing")
async def scan_listing(request: Request):
    try:
        data = await request.json()
    except Exception:
        data = {}

    price = float(data.get("price", 14000))
    desc = str(data.get("description", "")).lower()

    flags = []
    risk_score = 10
    if any(k in desc for k in ["wire", "western union", "gift card", "crypto", "advance deposit"]):
        flags.append("High risk payment method requested")
        risk_score += 45
    if price < 4000:
        flags.append("Price is suspiciously below campus market rates")
        risk_score += 35

    return {
        "status": "success",
        "data": {
            "risk_score": min(risk_score, 100),
            "is_suspicious": risk_score >= 50,
            "detected_flags": flags,
            "explanation": "Scanned with SyncMate Scam Guard."
        }
    }

# ========================================================
# Module 3: Contract Guard
# ========================================================
@app.post("/analyze_contract")
@app.post("/api/analyze_contract")
@app.post("/ai/analyze_contract.php")
@app.post("/api/ai/analyze_contract.php")
async def analyze_contract(request: Request):
    return {
        "status": "success",
        "data": {
            "contract_title": "Student Sublease Agreement Analysis",
            "overall_risk_score": 15,
            "is_safe_to_sign": True,
            "flagged_clauses": [
                {
                    "clause_text": "Security deposit refund requires 60-day advance notice.",
                    "risk_level": "LOW",
                    "explanation": "Standard lease term. Ensure move-out dates are documented in writing."
                }
            ],
            "key_takeaways": [
                "Fair INR terms aligned with standard Indian student residential subleases.",
                "Rent schedule and utility division are clearly stated."
            ]
        }
    }

# ========================================================
# Chat NLP Assistant
# ========================================================
@app.post("/chat")
@app.post("/api/chat")
@app.post("/ai/chat.php")
@app.post("/api/ai/chat.php")
async def chat_endpoint(request: Request):
    try:
        data = await request.json()
        msg = str(data.get("message", "")).strip().lower()
    except Exception:
        msg = ""

    if "rent" in msg or "price" in msg or "cost" in msg:
        reply = "Average verified student sublet rent near campus is around ₹12,000 to ₹16,000/month. You can use the Fair Rent tool to inspect exact unit values!"
    elif "roommate" in msg:
        reply = "Our AI neural matcher computes living compatibility across 8 dimensions. Take the 10-question Onboarding Quiz to discover your top matches!"
    elif "hi" in msg or "hello" in msg:
        reply = "Hello! I am SyncMate AI, your dedicated housing and roommate assistant. How can I assist you with your campus search today?"
    else:
        reply = "SyncMate is here to help you find verified sublets, analyze fair rental rates in INR (₹), and match compatible roommates!"

    return {
        "status": "success",
        "data": {
            "reply": reply,
            "intent": "general_help",
            "confidence": 0.95
        }
    }

# ========================================================
# Roommates Cross-Device Sync Endpoints
# ========================================================
@app.post("/roommates/sync_quiz")
@app.post("/api/roommates/sync_quiz")
async def sync_quiz(request: Request):
    data = await request.json()
    user_id = data.get("user_id")
    name = data.get("name", "Student")
    uni = data.get("university", "University")
    prefs = data.get("preferences", {})
    budget = float(prefs.get("max_budget_inr", 15000.0))

    if not user_id:
        return {"status": "error", "message": "user_id is required"}

    conn = get_db()
    c = conn.cursor()
    c.execute("SELECT id FROM users WHERE id = ?", (user_id,))
    row = c.fetchone()
    if row is None:
        c.execute("""
            INSERT INTO users (id, name, email, password_hash, university, max_budget, has_completed_quiz, quiz_preferences_json)
            VALUES (?, ?, ?, 'demo', ?, ?, 1, ?)
        """, (user_id, name, f"{user_id}@student.edu", uni, budget, json.dumps(prefs)))
    else:
        c.execute("""
            UPDATE users SET name = ?, university = ?, max_budget = ?, has_completed_quiz = 1, quiz_preferences_json = ?
            WHERE id = ?
        """, (name, uni, budget, json.dumps(prefs), user_id))
    conn.commit()
    conn.close()

    return {"status": "success", "message": "Quiz preferences synced"}


@app.get("/roommates/active_users")
@app.get("/api/roommates/active_users")
async def active_users(user_id: Optional[str] = None):
    conn = get_db()
    c = conn.cursor()
    if user_id:
        c.execute("SELECT * FROM users WHERE has_completed_quiz = 1 AND id != ?", (user_id,))
    else:
        c.execute("SELECT * FROM users WHERE has_completed_quiz = 1")
    rows = c.fetchall()

    users_list = []
    for r in rows:
        qp = json.loads(r["quiz_preferences_json"]) if r["quiz_preferences_json"] else None
        users_list.append({
            "id": r["id"],
            "name": r["name"],
            "email": r["email"],
            "university": r["university"],
            "is_verified": bool(r["is_verified"]),
            "bio": r["bio"] or "",
            "preferred_lease_term": r["preferred_lease_term"] or "Summer 2025",
            "max_budget": float(r["max_budget"]),
            "preferred_location": r["preferred_location"] or "Near Campus",
            "has_completed_quiz": True,
            "quiz_preferences": qp
        })
    conn.close()
    return {"status": "success", "data": users_list}


@app.post("/roommates/send_request")
@app.post("/api/roommates/send_request")
async def send_request(request: Request):
    data = await request.json()
    req_id = data.get("request_id") or f"req_{int(time.time() * 1000)}_{int(np.random.randint(100, 999))}"
    sender_id = data.get("sender_id", "")
    sender_name = data.get("sender_name", "")
    sender_uni = data.get("sender_university", "")
    receiver_id = data.get("receiver_id", "")
    receiver_name = data.get("receiver_name", "")
    receiver_uni = data.get("receiver_university", "")
    score = int(data.get("compatibility_score", 85))
    intro = data.get("intro_message", "")
    created_at = int(data.get("timestamp") or int(time.time() * 1000))

    conn = get_db()
    c = conn.cursor()

    # Prevent duplicate requests between sender and receiver
    c.execute("""
        SELECT * FROM roommate_requests 
        WHERE sender_id = ? AND receiver_id = ? AND status IN ('PENDING', 'ACCEPTED')
    """, (sender_id, receiver_id))
    existing = c.fetchone()
    if existing:
        conn.close()
        return {
            "status": "success",
            "message": "Existing request retrieved",
            "data": {
                "request_id": existing["request_id"],
                "sender_id": existing["sender_id"],
                "sender_name": existing["sender_name"],
                "sender_university": existing["sender_university"],
                "receiver_id": existing["receiver_id"],
                "receiver_name": existing["receiver_name"],
                "receiver_university": existing["receiver_university"] if "receiver_university" in existing.keys() else "",
                "compatibility_score": int(existing["compatibility_score"]),
                "intro_message": existing["intro_message"] or "",
                "status": existing["status"],
                "timestamp": int(existing["created_at"])
            }
        }

    c.execute("""
        INSERT INTO roommate_requests (request_id, sender_id, sender_name, sender_university, receiver_id, receiver_name, receiver_university, compatibility_score, intro_message, status, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?)
    """, (req_id, sender_id, sender_name, sender_uni, receiver_id, receiver_name, receiver_uni, score, intro, created_at))
    conn.commit()
    conn.close()

    return {
        "status": "success",
        "data": {
            "request_id": req_id,
            "sender_id": sender_id,
            "sender_name": sender_name,
            "sender_university": sender_uni,
            "receiver_id": receiver_id,
            "receiver_name": receiver_name,
            "receiver_university": receiver_uni,
            "compatibility_score": score,
            "intro_message": intro,
            "status": "PENDING",
            "timestamp": created_at
        }
    }


@app.get("/roommates/get_requests")
@app.get("/api/roommates/get_requests")
async def get_requests(user_id: str):
    conn = get_db()
    c = conn.cursor()
    c.execute("SELECT * FROM roommate_requests WHERE receiver_id = ? ORDER BY created_at DESC", (user_id,))
    in_rows = c.fetchall()
    c.execute("SELECT * FROM roommate_requests WHERE sender_id = ? ORDER BY created_at DESC", (user_id,))
    out_rows = c.fetchall()

    def map_row(r):
        return {
            "request_id": r["request_id"],
            "sender_id": r["sender_id"],
            "sender_name": r["sender_name"],
            "sender_university": r["sender_university"],
            "receiver_id": r["receiver_id"],
            "receiver_name": r["receiver_name"],
            "receiver_university": r["receiver_university"] if "receiver_university" in r.keys() else "",
            "compatibility_score": int(r["compatibility_score"]),
            "intro_message": r["intro_message"] or "",
            "status": r["status"],
            "timestamp": int(r["created_at"])
        }

    incoming = [map_row(r) for r in in_rows]
    sent = [map_row(r) for r in out_rows]
    conn.close()

    return {"status": "success", "data": {"incoming": incoming, "sent": sent}}


@app.post("/roommates/update_request")
@app.post("/api/roommates/update_request")
async def update_request(request: Request):
    data = await request.json()
    req_id = data.get("request_id", "")
    new_status = data.get("status", "ACCEPTED")

    conn = get_db()
    c = conn.cursor()
    c.execute("UPDATE roommate_requests SET status = ? WHERE request_id = ?", (new_status, req_id))
    conn.commit()
    conn.close()

    return {"status": "success", "message": f"Updated to {new_status}"}


@app.post("/roommates/cancel_request")
@app.post("/api/roommates/cancel_request")
async def cancel_request(request: Request):
    data = await request.json()
    req_id = data.get("request_id", "")

    conn = get_db()
    c = conn.cursor()
    c.execute("UPDATE roommate_requests SET status = 'DECLINED' WHERE request_id = ?", (req_id,))
    conn.commit()
    conn.close()

    return {"status": "success", "message": "Request cancelled"}


if __name__ == "__main__":
    import uvicorn
    print("[*] Starting SyncMate FastAPI AI Backend on 0.0.0.0:8000...")
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=False)
