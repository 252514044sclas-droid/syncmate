import os
import sys
import numpy as np
import pandas as pd
import joblib
from sklearn.ensemble import HistGradientBoostingRegressor
from sklearn.model_selection import KFold, cross_val_score, train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score

# Ensure UTF-8 output encoding
if sys.stdout.encoding != 'utf-8':
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

print("==================================================================")
print(" 🚀 SyncMate AI: 2026 Pan-India & Deep-Chennai Fair-Rent Regressor")
print("==================================================================")

np.random.seed(42)
N_SAMPLES = 15000

# -------------------------------------------------------------
# 1. Comprehensive Localities by City & Market Tier
# -------------------------------------------------------------
# Cities:
# 0 = Chennai (deep 28+ localities)
# 1 = Bengaluru
# 2 = Pune
# 3 = Mumbai & Navi Mumbai
# 4 = Delhi NCR
# 5 = Hyderabad
# 6 = Coimbatore & Other Student Hubs

CITIES = ["Chennai", "Bengaluru", "Pune", "Mumbai", "Delhi NCR", "Hyderabad", "Coimbatore & Student Hubs"]

# Map of Locality -> (City, Tier)
# Tiers:
# 0: Super-Premium / Ultra-Prime Central
# 1: Prime Urban / IT & Coastal Tech Corridors
# 2: Mid-tier Residential / West Suburbs / Tech Suburbs
# 3: Budget Student Hubs / Campus Perimeters

LOCALITY_DEFINITIONS = [
    # --- CHENNAI ---
    ("Anna Nagar", "Chennai", 0, 1),
    ("Adyar", "Chennai", 0, 1),
    ("Besant Nagar", "Chennai", 0, 1),
    ("Nungambakkam", "Chennai", 0, 1),
    ("T. Nagar", "Chennai", 0, 1),
    ("Alwarpet", "Chennai", 0, 1),
    ("Mylapore", "Chennai", 0, 1),
    ("Kilpauk", "Chennai", 0, 1),
    ("Shenoy Nagar", "Chennai", 0, 1),
    ("Velachery", "Chennai", 1, 2),
    ("Taramani", "Chennai", 1, 2),
    ("Thiruvanmiyur", "Chennai", 1, 2),
    ("Perungudi", "Chennai", 1, 2),
    ("Thoraipakkam", "Chennai", 1, 2),
    ("Sholinganallur", "Chennai", 1, 2),
    ("Navalur", "Chennai", 1, 2),
    ("Siruseri", "Chennai", 1, 2),
    ("ECR", "Chennai", 1, 2),
    ("Guindy", "Chennai", 1, 2),
    ("Porur", "Chennai", 2, 2),
    ("Valasaravakkam", "Chennai", 2, 2),
    ("Vadapalani", "Chennai", 2, 2),
    ("Koyambedu", "Chennai", 2, 2),
    ("Mogappair", "Chennai", 2, 2),
    ("Ambattur", "Chennai", 2, 2),
    ("Tambaram", "Chennai", 3, 3),
    ("Chromepet", "Chennai", 3, 3),
    ("Pallavaram", "Chennai", 3, 3),
    ("Potheri", "Chennai", 3, 3),
    ("Kattankulathur", "Chennai", 3, 3),
    ("Urapakkam", "Chennai", 3, 3),
    ("Avadi", "Chennai", 3, 3),

    # --- BENGALURU ---
    ("Koramangala", "Bengaluru", 0, 1),
    ("Indiranagar", "Bengaluru", 0, 1),
    ("HSR Layout", "Bengaluru", 1, 1),
    ("Bellandur", "Bengaluru", 1, 1),
    ("Whitefield", "Bengaluru", 1, 2),
    ("Marathahalli", "Bengaluru", 1, 2),
    ("BTM Layout", "Bengaluru", 2, 2),
    ("JP Nagar", "Bengaluru", 2, 2),
    ("Hebbal", "Bengaluru", 2, 2),
    ("Electronic City", "Bengaluru", 2, 3),
    ("Yelahanka", "Bengaluru", 3, 3),

    # --- PUNE ---
    ("Koregaon Park", "Pune", 0, 1),
    ("Baner", "Pune", 1, 1),
    ("Aundh", "Pune", 1, 1),
    ("Viman Nagar", "Pune", 1, 1),
    ("Kothrud", "Pune", 2, 2),
    ("Karve Nagar", "Pune", 2, 2),
    ("Wakad", "Pune", 2, 2),
    ("Hinjewadi", "Pune", 2, 2),
    ("Hadapsar", "Pune", 3, 3),

    # --- MUMBAI & NAVI MUMBAI ---
    ("Bandra", "Mumbai", 0, 0),
    ("Powai", "Mumbai", 0, 1),
    ("Andheri", "Mumbai", 0, 1),
    ("Goregaon", "Mumbai", 1, 1),
    ("Malad", "Mumbai", 1, 2),
    ("Thane", "Mumbai", 2, 2),
    ("Vashi", "Mumbai", 2, 2),
    ("Nerul", "Mumbai", 2, 3),
    ("Kharghar", "Mumbai", 3, 3),

    # --- DELHI NCR ---
    ("Hauz Khas", "Delhi NCR", 0, 1),
    ("Gurugram Cyber City", "Delhi NCR", 0, 1),
    ("North Campus", "Delhi NCR", 1, 1),
    ("Kamla Nagar", "Delhi NCR", 1, 1),
    ("Malviya Nagar", "Delhi NCR", 1, 1),
    ("Lajpat Nagar", "Delhi NCR", 1, 1),
    ("Noida Sector 62", "Delhi NCR", 2, 2),
    ("Noida Sector 125", "Delhi NCR", 2, 2),

    # --- HYDERABAD ---
    ("HITEC City", "Hyderabad", 1, 1),
    ("Gachibowli", "Hyderabad", 1, 1),
    ("Madhapur", "Hyderabad", 1, 1),
    ("Kondapur", "Hyderabad", 1, 2),
    ("Kukatpally", "Hyderabad", 2, 2),
    ("Ameerpet", "Hyderabad", 2, 2),
    ("Begumpet", "Hyderabad", 2, 2),

    # --- COIMBATORE & OTHER STUDENT HUBS ---
    ("Peelamedu", "Coimbatore", 3, 3),
    ("Saravanampatti", "Coimbatore", 3, 3),
    ("Gandhipuram", "Coimbatore", 3, 3),
    ("RS Puram", "Coimbatore", 2, 3),
    ("Vellore (VIT)", "Vellore", 3, 3),
    ("Manipal", "Manipal", 3, 3)
]

LOCALITY_LOOKUP = {}
for loc, city, tier, nat_tier in LOCALITY_DEFINITIONS:
    LOCALITY_LOOKUP[loc.lower()] = {
        "locality": loc,
        "city": city,
        "local_tier": tier,
        "national_tier": nat_tier
    }

ROOM_TYPES = ["Shared PG", "Private Room", "Studio", "1BHK", "2BHK", "3BHK"]
ROOM_TYPE_CODE = {
    "Shared PG": 0,
    "Private Room": 1,
    "Studio": 2,
    "1BHK": 3,
    "2BHK": 4,
    "3BHK": 5
}

print(f"[1/5] Synthesizing {N_SAMPLES} calibrated 2026 Pan-India & Chennai records...")

records = []
loc_keys = list(LOCALITY_LOOKUP.keys())

# Oversample Chennai so it represents ~40% of the dataset as requested
chennai_keys = [k for k in loc_keys if LOCALITY_LOOKUP[k]["city"] == "Chennai"]
other_keys = [k for k in loc_keys if LOCALITY_LOOKUP[k]["city"] != "Chennai"]

for _ in range(N_SAMPLES):
    if np.random.rand() < 0.40:
        lkey = np.random.choice(chennai_keys)
    else:
        lkey = np.random.choice(other_keys)
        
    info = LOCALITY_LOOKUP[lkey]
    loc = info["locality"]
    city = info["city"]
    tier = info["local_tier"]
    nat_tier = info["national_tier"]

    room_type = np.random.choice(ROOM_TYPES, p=[0.20, 0.25, 0.12, 0.20, 0.15, 0.08])
    
    # Area sqft & bedrooms
    if room_type == "Shared PG":
        sqft = int(np.random.randint(120, 260))
        bedrooms = 1
        bathrooms = 1.0
    elif room_type == "Private Room":
        sqft = int(np.random.randint(180, 360))
        bedrooms = 1
        bathrooms = 1.0
    elif room_type == "Studio":
        sqft = int(np.random.randint(350, 550))
        bedrooms = 1
        bathrooms = 1.0
    elif room_type == "1BHK":
        sqft = int(np.random.randint(460, 720))
        bedrooms = 1
        bathrooms = 1.0
    elif room_type == "2BHK":
        sqft = int(np.random.randint(750, 1150))
        bedrooms = 2
        bathrooms = np.random.choice([1.0, 2.0], p=[0.25, 0.75])
    else:  # 3BHK
        sqft = int(np.random.randint(1180, 1800))
        bedrooms = 3
        bathrooms = np.random.choice([2.0, 3.0], p=[0.30, 0.70])

    lease_duration = int(np.random.choice([1, 2, 3, 6, 9, 11, 12], p=[0.08, 0.08, 0.20, 0.24, 0.10, 0.25, 0.05]))

    # -------------------------------------------------------------
    # Calibrated Price Bands per City and Tier
    # -------------------------------------------------------------
    if city == "Mumbai":
        if tier == 0 and loc == "Bandra":
            bands = {"Shared PG": (15000, 24000), "Private Room": (35000, 55000), "Studio": (38000, 58000), "1BHK": (40000, 62000), "2BHK": (60000, 95000), "3BHK": (95000, 150000)}
        elif tier == 0:  # Powai, Andheri
            bands = {"Shared PG": (12000, 18000), "Private Room": (25000, 38000), "Studio": (26000, 42000), "1BHK": (28000, 45000), "2BHK": (45000, 68000), "3BHK": (68000, 98000)}
        elif tier == 1:  # Goregaon, Malad
            bands = {"Shared PG": (10000, 15000), "Private Room": (18000, 28000), "Studio": (20000, 30000), "1BHK": (22000, 32000), "2BHK": (35000, 52000), "3BHK": (52000, 75000)}
        else:  # Thane, Navi Mumbai, Kharghar
            bands = {"Shared PG": (7500, 12000), "Private Room": (13000, 20000), "Studio": (14000, 22000), "1BHK": (14000, 22000), "2BHK": (22000, 35000), "3BHK": (34000, 50000)}

    elif city == "Bengaluru":
        if tier == 0:  # Koramangala, Indiranagar
            bands = {"Shared PG": (10000, 16000), "Private Room": (20000, 32000), "Studio": (22000, 34000), "1BHK": (22000, 33000), "2BHK": (35000, 55000), "3BHK": (55000, 80000)}
        elif tier == 1:  # HSR, Bellandur, Whitefield, Marathahalli
            bands = {"Shared PG": (8500, 14000), "Private Room": (16000, 25000), "Studio": (17500, 26000), "1BHK": (17000, 25000), "2BHK": (28000, 45000), "3BHK": (42000, 65000)}
        elif tier == 2:  # BTM, JP Nagar, Hebbal, Electronic City
            bands = {"Shared PG": (7000, 11500), "Private Room": (12000, 18500), "Studio": (13000, 19500), "1BHK": (13000, 19000), "2BHK": (20000, 32000), "3BHK": (32000, 48000)}
        else:  # Yelahanka
            bands = {"Shared PG": (6000, 9500), "Private Room": (9500, 15000), "Studio": (11000, 16000), "1BHK": (10000, 16000), "2BHK": (17000, 26000), "3BHK": (25000, 38000)}

    elif city == "Delhi NCR":
        if tier == 0:  # Hauz Khas, Gurugram Cyber City
            bands = {"Shared PG": (10000, 16000), "Private Room": (22000, 35000), "Studio": (24000, 36000), "1BHK": (24000, 36000), "2BHK": (38000, 58000), "3BHK": (58000, 85000)}
        elif tier == 1:  # North Campus, Kamla Nagar, Malviya Nagar, Lajpat Nagar
            bands = {"Shared PG": (8500, 14000), "Private Room": (15000, 24000), "Studio": (16000, 25000), "1BHK": (16000, 25000), "2BHK": (26000, 40000), "3BHK": (40000, 60000)}
        else:  # Noida Sector 62 / 125
            bands = {"Shared PG": (7000, 11000), "Private Room": (11000, 18000), "Studio": (12000, 19000), "1BHK": (12000, 18000), "2BHK": (20000, 30000), "3BHK": (30000, 44000)}

    elif city == "Pune":
        if tier == 0:  # Koregaon Park
            bands = {"Shared PG": (9000, 15000), "Private Room": (18000, 28000), "Studio": (20000, 30000), "1BHK": (20000, 29000), "2BHK": (32000, 50000), "3BHK": (48000, 70000)}
        elif tier == 1:  # Baner, Aundh, Viman Nagar
            bands = {"Shared PG": (8000, 13000), "Private Room": (15000, 23000), "Studio": (16000, 24000), "1BHK": (16000, 24000), "2BHK": (26000, 40000), "3BHK": (38000, 56000)}
        elif tier == 2:  # Kothrud, Karve Nagar, Wakad, Hinjewadi
            bands = {"Shared PG": (7000, 11000), "Private Room": (12000, 18000), "Studio": (13000, 19000), "1BHK": (12500, 18500), "2BHK": (20000, 30000), "3BHK": (30000, 42000)}
        else:  # Hadapsar
            bands = {"Shared PG": (6000, 9500), "Private Room": (9000, 14500), "Studio": (10500, 15500), "1BHK": (10000, 15000), "2BHK": (16000, 24000), "3BHK": (24000, 34000)}

    elif city == "Hyderabad":
        if tier == 1:  # HITEC City, Gachibowli, Madhapur, Kondapur
            bands = {"Shared PG": (8000, 13000), "Private Room": (16000, 25000), "Studio": (17000, 26000), "1BHK": (16500, 25500), "2BHK": (26000, 42000), "3BHK": (40000, 60000)}
        else:  # Kukatpally, Ameerpet, Begumpet
            bands = {"Shared PG": (6500, 10000), "Private Room": (11000, 17000), "Studio": (12000, 18000), "1BHK": (11500, 17500), "2BHK": (18000, 28000), "3BHK": (27000, 38000)}

    elif city in ["Coimbatore", "Vellore", "Manipal"]:
        # Budget student college campuses
        bands = {"Shared PG": (5000, 9000), "Private Room": (7500, 13000), "Studio": (8500, 14000), "1BHK": (8000, 13500), "2BHK": (13000, 20000), "3BHK": (20000, 28000)}

    else:  # CHENNAI (Default)
        if tier == 0:  # Anna Nagar, Adyar, Nungambakkam, T. Nagar, etc.
            bands = {"Shared PG": (9000, 14000), "Private Room": (18000, 25000), "Studio": (19000, 27000), "1BHK": (20000, 26500), "2BHK": (28000, 45000), "3BHK": (46000, 70000)}
        elif tier == 1:  # Velachery, Taramani, OMR, ECR, Guindy
            bands = {"Shared PG": (7500, 12000), "Private Room": (12000, 18000), "Studio": (14000, 19500), "1BHK": (13000, 18500), "2BHK": (18000, 32000), "3BHK": (32000, 48000)}
        elif tier == 2:  # Porur, Vadapalani, Koyambedu, Ambattur
            bands = {"Shared PG": (6500, 10500), "Private Room": (10000, 16000), "Studio": (11500, 17000), "1BHK": (11000, 16500), "2BHK": (16000, 26000), "3BHK": (26000, 38000)}
        else:  # Tambaram, Potheri/SRM, Chromepet, Avadi
            bands = {"Shared PG": (6000, 10000), "Private Room": (8000, 14000), "Studio": (9500, 14500), "1BHK": (8500, 14000), "2BHK": (14000, 22000), "3BHK": (22000, 32000)}

    low, high = bands[room_type]
    rent = np.random.uniform(low, high)

    # Short-term sublease premium
    if lease_duration <= 3:
        rent *= np.random.uniform(1.05, 1.10)
    elif lease_duration <= 6:
        rent *= np.random.uniform(1.02, 1.04)

    # Square footage micro-variation
    sqft_factor = 1.0 + ((sqft - 600) * 0.00015)
    rent *= np.clip(sqft_factor, 0.94, 1.08)

    # Gaussian noise
    rent += np.random.normal(0, 250)
    final_rent = float(np.round(rent, -2))

    city_code = CITIES.index(city) if city in CITIES else (0 if city == "Chennai" else 6)

    records.append({
        "location": loc,
        "city": city,
        "city_code": city_code,
        "locality_tier": tier,
        "national_tier": nat_tier,
        "room_type": room_type,
        "room_type_code": ROOM_TYPE_CODE[room_type],
        "area_sqft": sqft,
        "bedrooms": bedrooms,
        "bathrooms": bathrooms,
        "lease_duration_months": lease_duration,
        "monthly_rent_inr": final_rent
    })

df = pd.DataFrame(records)

# Save rental dataset
csv_paths = [
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "rental_dataset.csv"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "chennai_rental_dataset.csv"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "backend", "ml", "rental_dataset.csv"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "backend", "ml", "chennai_rental_dataset.csv")
]

for p in csv_paths:
    try:
        os.makedirs(os.path.dirname(p), exist_ok=True)
        df.to_csv(p, index=False)
        print(f"      Saved {len(df)} records to: {p}")
    except Exception:
        pass

# -------------------------------------------------------------
# 2. Train HistGradientBoostingRegressor
# -------------------------------------------------------------
feature_cols = ["city_code", "locality_tier", "area_sqft", "bedrooms", "room_type_code", "lease_duration_months"]
X = df[feature_cols].values
y = df["monthly_rent_inr"].values

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

print("[2/5] Running 5-Fold Cross Validation...")
kfold = KFold(n_splits=5, shuffle=True, random_state=42)
cv_model = HistGradientBoostingRegressor(max_iter=300, random_state=42)
cv_scores = cross_val_score(cv_model, X, y, cv=kfold, scoring='r2')
print(f"      5-Fold CV Score: {cv_scores.mean() * 100:.2f}% ± {cv_scores.std() * 100:.2f}%")

print("[3/5] Fitting Pan-India HistGradientBoostingRegressor Model...")
model = HistGradientBoostingRegressor(
    max_iter=350,
    learning_rate=0.06,
    max_depth=12,
    min_samples_leaf=12,
    l2_regularization=0.05,
    random_state=42
)
model.fit(X_train, y_train)

print("[4/5] Evaluating Performance Metrics...")
y_pred = model.predict(X_test)
r2 = r2_score(y_test, y_pred)
mae = mean_absolute_error(y_test, y_pred)
rmse = np.sqrt(mean_squared_error(y_test, y_pred))

print("------------------------------------------------------------------")
print(f"  🏆 Pan-India Fair-Rent Accuracy: {r2 * 100:.2f}% (R^2 = {r2:.4f})")
print(f"  * Mean Absolute Error (MAE):     INR ₹{mae:.2f}")
print(f"  * Root Mean Square Error (RMSE): INR ₹{rmse:.2f}")
print("------------------------------------------------------------------")

# -------------------------------------------------------------
# 3. Serialize Model Artifacts
# -------------------------------------------------------------
print("[5/5] Serializing model and encoders...")

model_artifact = {
    "model": model,
    "feature_columns": feature_cols,
    "locality_lookup": LOCALITY_LOOKUP,
    "room_type_code": ROOM_TYPE_CODE,
    "cities": CITIES,
    "version": "2026.2-pan-india-chennai-fair-rent-regressor",
    "metrics": {
        "r2_score": float(r2),
        "mae_inr": float(mae),
        "rmse_inr": float(rmse),
        "cv_mean_r2": float(cv_scores.mean())
    }
}

target_paths = [
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "fair_price_model.pkl"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "backend", "ml", "fair_price_model.pkl"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "backend", "ml", "rent_model.pkl"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "rent_model.pkl")
]

for path in target_paths:
    try:
        os.makedirs(os.path.dirname(path), exist_ok=True)
        joblib.dump(model_artifact, path)
        print(f"      Saved model artifact to: {path}")
    except Exception as e:
        print(f"      Warning saving to {path}: {e}")

print("==================================================================")
print(" ✅ Pan-India & Chennai Fair-Rent Pipeline Training Complete!")
print("==================================================================")
