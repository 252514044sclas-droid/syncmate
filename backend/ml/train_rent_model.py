import sys
import os
# Ensure UTF-8 output encoding for Windows terminals
if sys.stdout.encoding != 'utf-8':
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

import numpy as np
import pandas as pd
import joblib
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score

print("==========================================================")
print("  SyncMate AI: Training Fair-Rent & Utility Prediction Model")
print("==========================================================")

# Set random seed for reproducible training
np.random.seed(42)
n_samples = 3000

# Feature 1: Square footage (150 sqft for shared rooms to 1400 sqft for entire apartments)
square_feet = np.random.randint(180, 1350, size=n_samples)

# Feature 2: Bedrooms (1 to 3)
bedrooms = np.random.choice([1, 2, 3], size=n_samples, p=[0.55, 0.35, 0.10])

# Feature 3: Bathrooms (1.0, 1.5, 2.0)
bathrooms = np.random.choice([1.0, 1.5, 2.0], size=n_samples, p=[0.60, 0.25, 0.15])

# Feature 4: Room Type Code
# 0 = Shared Room (₹5,000 - ₹9,000)
# 1 = Private Room (₹8,000 - ₹16,000)
# 2 = Studio (₹14,000 - ₹24,000)
# 3 = Entire Apartment (₹18,000 - ₹34,000)
room_type = np.random.choice([0, 1, 2, 3], size=n_samples, p=[0.25, 0.45, 0.15, 0.15])

# Feature 5: City / Location Tier Code
# 0 = Standard Campus Zone (Chennai Adyar, Delhi North Campus, Pune Kothrud)
# 1 = High-Demand Metro / Tech Hub (Bengaluru Koramangala, Hyderabad Hitec City)
# 2 = Suburban / Budget Campus Perimeter (>3km)
location_tier = np.random.choice([0, 1, 2], size=n_samples, p=[0.45, 0.35, 0.20])

# Feature 6: AC / Climate Amenities
# 0 = Non-AC, 1 = AC Installed
has_ac = np.random.choice([0, 1], size=n_samples, p=[0.40, 0.60])

# Feature 7: Semester Season Code
# 0 = Summer / Vacation Break (8-10% rental discounts)
# 1 = Peak Semester Intake (July-August / January rush)
season = np.random.choice([0, 1], size=n_samples, p=[0.35, 0.65])

# Calculate Target Rent in Indian Rupees (INR) based on real student market economics
base_rate_per_sqft = np.where(location_tier == 1, 23.5,
                     np.where(location_tier == 0, 18.0, 13.5))

base_rent = (square_feet * base_rate_per_sqft) + (bedrooms * 2200) + (bathrooms * 1400)

type_multiplier = np.where(room_type == 0, 0.58,   # Shared
                  np.where(room_type == 1, 0.88,   # Private
                  np.where(room_type == 2, 1.25,   # Studio
                                           1.50))) # Entire Apt

location_multiplier = np.where(location_tier == 1, 1.22,
                      np.where(location_tier == 0, 1.05, 0.88))

ac_addon = np.where(has_ac == 1, 1500, 0)
season_multiplier = np.where(season == 1, 1.07, 0.93)

target_rent = (base_rent * type_multiplier * location_multiplier * season_multiplier) + ac_addon

# Add natural market variation (Gaussian noise)
target_rent += np.random.normal(0, 450, size=n_samples)

# Bound realistic student housing rental prices (₹5,500 - ₹35,000)
target_rent = np.clip(target_rent, 5500, 35000)
target_rent = np.round(target_rent, -2) # Round to nearest 100

# Assemble DataFrame
data = pd.DataFrame({
    'square_feet': square_feet,
    'bedrooms': bedrooms,
    'bathrooms': bathrooms,
    'room_type': room_type,
    'location_tier': location_tier,
    'has_ac': has_ac,
    'season': season,
    'monthly_rent_inr': target_rent
})

# Save generated training dataset for inspection
dataset_path = os.path.join(os.path.dirname(__file__), 'housing_training_data.csv')
data.to_csv(dataset_path, index=False)
print(f"[1/4] Generated synthetic Indian Student Housing Dataset ({n_samples} samples)")
print(f"      Saved to: {dataset_path}")

# Feature matrix X and target vector y
feature_columns = ['square_feet', 'bedrooms', 'bathrooms', 'room_type', 'location_tier', 'has_ac', 'season']
X = data[feature_columns].values
y = data['monthly_rent_inr'].values

# Split dataset: 80% train, 20% test
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
print(f"[2/4] Train size: {len(X_train)} | Test size: {len(X_test)}")

# Train Scikit-Learn Random Forest Regressor
print("[3/4] Training Random Forest Regressor (n_estimators=150, max_depth=12)...")
model = RandomForestRegressor(
    n_estimators=150,
    max_depth=12,
    min_samples_split=4,
    min_samples_leaf=2,
    random_state=42,
    n_jobs=-1
)
model.fit(X_train, y_train)

# Evaluate model performance on unseen test data
y_pred = model.predict(X_test)
r2 = r2_score(y_test, y_pred)
mae = mean_absolute_error(y_test, y_pred)
rmse = np.sqrt(mean_squared_error(y_test, y_pred))

print("----------------------------------------------------------")
print("  Model Training & Evaluation Results")
print("----------------------------------------------------------")
print(f"  * R^2 Score (Accuracy):   {r2 * 100:.2f}% (R^2 = {r2:.4f})")
print(f"  * Mean Absolute Error:   INR {mae:.2f}")
print(f"  * Root Mean Sq Error:    INR {rmse:.2f}")
print("----------------------------------------------------------")

# Feature importances
print("  Feature Importance Breakdown:")
for name, importance in zip(feature_columns, model.feature_importances_):
    bar = "=" * int(importance * 30)
    print(f"    - {name:<15}: {importance * 100:5.2f}% |{bar}")
print("----------------------------------------------------------")

# Save trained model artifact
model_path = os.path.join(os.path.dirname(__file__), 'rent_model.pkl')
joblib.dump(model, model_path)
print(f"[4/4] Successfully serialized model to: {model_path}")
print("Training process finished successfully.")
