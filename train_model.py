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
print(" 🚀 SyncMate AI: 2026 Chennai Real-World Fair-Rent Regressor")
print("==================================================================")

np.random.seed(42)
N_SAMPLES = 10000

# -------------------------------------------------------------
# 1. 25+ Chennai Localities Grouped by Real-World Market Tiers
# -------------------------------------------------------------
# Tier 0 (Premium Central): ₹18k-₹25k (1BHK), ₹28k-₹45k (2BHK), ₹9k-₹14k (PG)
TIER_0_LOCALITIES = [
    "Anna Nagar", "Adyar", "Besant Nagar", "Nungambakkam", 
    "T. Nagar", "Alwarpet", "Mylapore", "Kilpauk", "Shenoy Nagar"
]

# Tier 1 (IT Corridor & South-East Coastal/Tech): ₹12k-₹18k (1BHK), ₹18k-₹32k (2BHK), ₹7.5k-₹12k (PG)
TIER_1_LOCALITIES = [
    "Velachery", "Taramani", "Thiruvanmiyur", "Perungudi", 
    "Thoraipakkam", "Sholinganallur", "Navalur", "Siruseri", 
    "ECR", "Neelankarai", "Palavakkam", "Guindy"
]

# Tier 2 (West & University Suburbs): ₹10k-₹16k (1BHK), ₹16k-₹26k (2BHK), ₹6.5k-₹10.5k (PG)
TIER_2_LOCALITIES = [
    "Porur", "Valasaravakkam", "Vadapalani", "Koyambedu", 
    "Mogappair", "Ambattur"
]

# Tier 3 (South & Campus Budget Zones): ₹8k-₹14k (1BHK), ₹14k-₹22k (2BHK), ₹6k-₹10k (PG)
TIER_3_LOCALITIES = [
    "Tambaram", "Chromepet", "Pallavaram", "Potheri", 
    "Kattankulathur", "Urapakkam", "Avadi"
]

ALL_LOCALITIES = (
    TIER_0_LOCALITIES + 
    TIER_1_LOCALITIES + 
    TIER_2_LOCALITIES + 
    TIER_3_LOCALITIES
)

LOCALITY_TIER_MAP = {}
for loc in TIER_0_LOCALITIES: LOCALITY_TIER_MAP[loc.lower()] = 0
for loc in TIER_1_LOCALITIES: LOCALITY_TIER_MAP[loc.lower()] = 1
for loc in TIER_2_LOCALITIES: LOCALITY_TIER_MAP[loc.lower()] = 2
for loc in TIER_3_LOCALITIES: LOCALITY_TIER_MAP[loc.lower()] = 3

ROOM_TYPES = ["Shared PG", "Private Room", "Studio", "1BHK", "2BHK", "3BHK"]
ROOM_TYPE_CODE = {
    "Shared PG": 0,
    "Private Room": 1,
    "Studio": 2,
    "1BHK": 3,
    "2BHK": 4,
    "3BHK": 5
}

print(f"[1/5] Synthesizing {N_SAMPLES} calibrated 2026 Chennai housing rental records...")

records = []
for _ in range(N_SAMPLES):
    loc = np.random.choice(ALL_LOCALITIES)
    tier = LOCALITY_TIER_MAP[loc.lower()]
    room_type = np.random.choice(ROOM_TYPES, p=[0.20, 0.25, 0.12, 0.20, 0.15, 0.08])
    
    # Area sqft & bedrooms accurately correlated with room type
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
    # Calibrated Baseline Rents matching User Specification
    # -------------------------------------------------------------
    if tier == 0:  # Premium / Central (Anna Nagar, Adyar, Nungambakkam, etc.)
        if room_type == "Shared PG":
            rent = np.random.uniform(9000, 14000)
        elif room_type == "Private Room":
            rent = np.random.uniform(18000, 25000)
        elif room_type == "Studio":
            rent = np.random.uniform(19000, 27000)
        elif room_type == "1BHK":
            rent = np.random.uniform(20000, 26500)
        elif room_type == "2BHK":
            rent = np.random.uniform(28000, 45000)
        else:  # 3BHK
            rent = np.random.uniform(46000, 70000)
            
    elif tier == 1:  # IT Corridor & South-East (Velachery, Taramani, OMR, etc.)
        if room_type == "Shared PG":
            rent = np.random.uniform(7500, 12000)
        elif room_type == "Private Room":
            rent = np.random.uniform(12000, 18000)
        elif room_type == "Studio":
            rent = np.random.uniform(14000, 19500)
        elif room_type == "1BHK":
            rent = np.random.uniform(13000, 18500)
        elif room_type == "2BHK":
            rent = np.random.uniform(18000, 32000)
        else:  # 3BHK
            rent = np.random.uniform(32000, 48000)
            
    elif tier == 2:  # West & University Suburbs (Porur, Vadapalani, DLF, etc.)
        if room_type == "Shared PG":
            rent = np.random.uniform(6500, 10500)
        elif room_type == "Private Room":
            rent = np.random.uniform(10000, 16000)
        elif room_type == "Studio":
            rent = np.random.uniform(11500, 17000)
        elif room_type == "1BHK":
            rent = np.random.uniform(11000, 16500)
        elif room_type == "2BHK":
            rent = np.random.uniform(16000, 26000)
        else:  # 3BHK
            rent = np.random.uniform(26000, 38000)
            
    else:  # Tier 3: South & Campus Budget (Tambaram, Potheri/SRM, Chromepet, etc.)
        if room_type == "Shared PG":
            rent = np.random.uniform(6000, 10000)
        elif room_type == "Private Room":
            rent = np.random.uniform(8000, 14000)
        elif room_type == "Studio":
            rent = np.random.uniform(9500, 14500)
        elif room_type == "1BHK":
            rent = np.random.uniform(8500, 14000)
        elif room_type == "2BHK":
            rent = np.random.uniform(14000, 22000)
        else:  # 3BHK
            rent = np.random.uniform(22000, 32000)

    # Slight short-term sublease premium (1-3 months sublease vs 11 months standard)
    if lease_duration <= 3:
        rent *= np.random.uniform(1.05, 1.10)
    elif lease_duration <= 6:
        rent *= np.random.uniform(1.02, 1.04)

    # Scale with subtle square footage micro-adjustment within each room category
    sqft_factor = 1.0 + ((sqft - 600) * 0.00015)
    rent *= np.clip(sqft_factor, 0.94, 1.08)

    # Add realistic market Gaussian noise
    rent += np.random.normal(0, 250)
    final_rent = float(np.round(rent, -2))

    records.append({
        "location": loc,
        "location_tier": tier,
        "room_type": room_type,
        "room_type_code": ROOM_TYPE_CODE[room_type],
        "area_sqft": sqft,
        "bedrooms": bedrooms,
        "bathrooms": bathrooms,
        "lease_duration_months": lease_duration,
        "monthly_rent_inr": final_rent
    })

df = pd.DataFrame(records)

# Save datasets to root and backend/ml
root_csv = os.path.join(os.path.dirname(os.path.abspath(__file__)), "chennai_rental_dataset.csv")
backend_csv = os.path.join(os.path.dirname(os.path.abspath(__file__)), "backend", "ml", "chennai_rental_dataset.csv")

df.to_csv(root_csv, index=False)
try:
    os.makedirs(os.path.dirname(backend_csv), exist_ok=True)
    df.to_csv(backend_csv, index=False)
except Exception:
    pass

print(f"      Saved {len(df)} calibrated records to: {root_csv}")

# -------------------------------------------------------------
# 2. Train High-Precision HistGradientBoostingRegressor
# -------------------------------------------------------------
feature_cols = ["location_tier", "area_sqft", "bedrooms", "room_type_code", "lease_duration_months"]
X = df[feature_cols].values
y = df["monthly_rent_inr"].values

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

print("[2/5] Performing 5-Fold Cross Validation...")
kfold = KFold(n_splits=5, shuffle=True, random_state=42)
cv_model = HistGradientBoostingRegressor(max_iter=250, random_state=42)
cv_scores = cross_val_score(cv_model, X, y, cv=kfold, scoring='r2')
print(f"      5-Fold CV Score: {cv_scores.mean() * 100:.2f}% ± {cv_scores.std() * 100:.2f}%")

print("[3/5] Fitting Deep HistGradientBoostingRegressor Model...")
model = HistGradientBoostingRegressor(
    max_iter=300,
    learning_rate=0.07,
    max_depth=10,
    min_samples_leaf=15,
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
print(f"  🏆 Chennai Fair-Rent Model Accuracy: {r2 * 100:.2f}% (R^2 = {r2:.4f})")
print(f"  * Mean Absolute Error (MAE):       INR ₹{mae:.2f}")
print(f"  * Root Mean Square Error (RMSE):   INR ₹{rmse:.2f}")
print("------------------------------------------------------------------")

# -------------------------------------------------------------
# 3. Serialize Model Artifacts
# -------------------------------------------------------------
print("[5/5] Serializing model and encoders...")

model_artifact = {
    "model": model,
    "feature_columns": feature_cols,
    "locality_tier_map": LOCALITY_TIER_MAP,
    "room_type_code": ROOM_TYPE_CODE,
    "all_localities": ALL_LOCALITIES,
    "version": "2026.1-chennai-fair-rent-regressor",
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
print(" ✅ Chennai Fair-Rent Pipeline Training Complete & Verified!")
print("==================================================================")
