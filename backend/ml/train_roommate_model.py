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
from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import KFold, cross_val_score, train_test_split
from sklearn.metrics import mean_absolute_error, r2_score

print("==================================================================")
print(" 👥 SyncMate AI: Deep Roommate Neural Network Model (10,000 Pairs)")
print("==================================================================")

np.random.seed(42)
n_pairs = 10000

print(f"[1/5] Generating Deep Lifestyle Compatibility Dataset ({n_pairs} pairs)...")

# Feature 1: sleep_delta (0.0 to 1.0)
sleep_delta = np.random.uniform(0.0, 1.0, size=n_pairs)

# Feature 2: cleanliness_delta (0.0 to 1.0)
cleanliness_delta = np.random.uniform(0.0, 1.0, size=n_pairs)

# Feature 3: noise_study_delta (0.0 to 1.0)
noise_study_delta = np.random.uniform(0.0, 1.0, size=n_pairs)

# Feature 4: smoking_conflict (0 or 1)
smoking_conflict = np.random.choice([0, 1], size=n_pairs, p=[0.75, 0.25])

# Feature 5: diet_compat (1.0 = same, 0.7 = tolerant, 0.3 = mismatch)
diet_compat = np.random.choice([1.0, 0.7, 0.3], size=n_pairs, p=[0.55, 0.30, 0.15])

# Feature 6: budget_similarity (0.3 to 1.0)
budget_similarity = np.random.uniform(0.3, 1.0, size=n_pairs)

# Feature 7: location_proximity (1.0 = same quadrant, 0.7 = metro, 0.2 = far)
location_proximity = np.random.choice([1.0, 0.7, 0.2], size=n_pairs, p=[0.45, 0.35, 0.20])

# Feature 8: university_match (0 or 1)
university_match = np.random.choice([0, 1], size=n_pairs, p=[0.40, 0.60])

# Compute Target Synergy Score (35% to 98%)
score = (
    (1.0 - sleep_delta) * 18.0 +
    (1.0 - cleanliness_delta) * 16.0 +
    (1.0 - noise_study_delta) * 14.0 +
    (1.0 - smoking_conflict) * 15.0 +
    diet_compat * 8.0 +
    budget_similarity * 15.0 +
    location_proximity * 10.0 +
    university_match * 4.0
)

score += np.random.normal(0, 1.2, size=n_pairs)
target_score = np.clip(score, 35, 98)
target_score = np.round(target_score, 1)

data = pd.DataFrame({
    'sleep_delta': sleep_delta,
    'cleanliness_delta': cleanliness_delta,
    'noise_study_delta': noise_study_delta,
    'smoking_conflict': smoking_conflict,
    'diet_compat': diet_compat,
    'budget_similarity': budget_similarity,
    'location_proximity': location_proximity,
    'university_match': university_match,
    'compatibility_score': target_score
})

dataset_csv_path = os.path.join(os.path.dirname(__file__), 'roommate_training_data.csv')
data.to_csv(dataset_csv_path, index=False)
print(f"      Saved {n_pairs} roommate training pairs to: {dataset_csv_path}")

feature_cols = ['sleep_delta', 'cleanliness_delta', 'noise_study_delta', 'smoking_conflict', 'diet_compat', 'budget_similarity', 'location_proximity', 'university_match']
X = data[feature_cols].values
y = data['compatibility_score'].values

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

print("[2/5] Training 3-Layer Deep Neural Network (MLPRegressor 128x64x32)...")
mlp_model = MLPRegressor(
    hidden_layer_sizes=(128, 64, 32),
    activation='relu',
    solver='adam',
    max_iter=500,
    random_state=42,
    early_stopping=True,
    validation_fraction=0.1
)
mlp_model.fit(X_train, y_train)

print("[3/5] Evaluating Neural Network Match Accuracy...")
y_pred = mlp_model.predict(X_test)
r2 = r2_score(y_test, y_pred)
mae = mean_absolute_error(y_test, y_pred)

print("----------------------------------------------------------")
print(f"  🏆 Neural Matcher Accuracy:      {r2 * 100:.2f}% (R^2 = {r2:.4f})")
print(f"  * Mean Absolute Error (MAE):    {mae:.2f}% score deviation")
print("----------------------------------------------------------")

model_artifact = {
    "model": mlp_model,
    "feature_columns": feature_cols,
    "version": "2.0.0-deep-neural-roommate-matcher"
}

model_save_path = os.path.join(os.path.dirname(__file__), 'roommate_matcher.pkl')
joblib.dump(model_artifact, model_save_path)
print(f"[4/5] Successfully serialized neural model artifact to: {model_save_path}")
print("Deep training completed successfully!")
