import numpy as np
import joblib
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split

print("🤖 Training SyncMate AI Rent & Utility Prediction Model...")

# Generate realistic synthetic training dataset for Indian Student Housing
# Features: [square_feet, bedrooms, bathrooms, room_type_code, location_code, season_code]
# Room Types: 0 = Shared Room, 1 = Private Room, 2 = Studio, 3 = Entire 2BHK/3BHK
# Locations: 0 = Campus Radius (<1km), 1 = Metro Hub, 2 = Suburban

np.random.seed(42)
n_samples = 1500

square_feet = np.random.randint(150, 1200, size=n_samples)
bedrooms = np.random.randint(1, 4, size=n_samples)
bathrooms = np.random.choice([1.0, 1.5, 2.0], size=n_samples)
room_type = np.random.choice([0, 1, 2, 3], size=n_samples, p=[0.25, 0.45, 0.15, 0.15])
location = np.random.choice([0, 1, 2], size=n_samples, p=[0.5, 0.3, 0.2])
season = np.random.choice([0, 1], size=n_samples) # 0 = Semester Break (Cheaper), 1 = Peak Term

# Calculate Target Rent (INR) with realistic weights & non-linear features
base_rent = (square_feet * 18.5) + (bedrooms * 2200) + (bathrooms * 1200)

type_multiplier = np.where(room_type == 0, 0.65,
                  np.where(room_type == 1, 0.90,
                  np.where(room_type == 2, 1.30, 1.55)))

location_multiplier = np.where(location == 0, 1.15,
                       np.where(location == 1, 1.25, 0.85))

season_multiplier = np.where(season == 1, 1.08, 0.94)

rent_inr = base_rent * type_multiplier * location_multiplier * season_multiplier
rent_inr += np.random.normal(0, 500, size=n_samples) # Add Gaussian noise
rent_inr = np.clip(rent_inr, 6000, 35000)

X = np.column_stack((square_feet, bedrooms, bathrooms, room_type, location, season))
y = rent_inr

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

model = RandomForestRegressor(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

score = model.score(X_test, y_test)
print(f"✅ Model Training Complete! R^2 Score: {score:.4f}")

joblib.dump(model, 'rent_model.pkl')
print("💾 Saved ML Model to 'backend/ml/rent_model.pkl'")
