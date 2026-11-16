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
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, r2_score

print("==========================================================")
print("  SyncMate AI: Training Roommate Compatibility Model (Module 4)")
print("==========================================================")

# Set random seed for reproducible training
np.random.seed(42)
n_pairs = 3500

# Generate synthetic student pairs with diverse lifestyle attributes:
# 1. sleep_delta: abs difference in sleep cycle (0 = both night owls/early risers, 1 = total opposite)
sleep_delta = np.random.uniform(0.0, 1.0, size=n_pairs)

# 2. cleanliness_delta: abs difference in cleanliness scale (0 = exact match, 1 = big difference)
cleanliness_delta = np.random.uniform(0.0, 1.0, size=n_pairs)

# 3. noise_study_delta: abs difference in study noise preferences
noise_study_delta = np.random.uniform(0.0, 1.0, size=n_pairs)

# 4. smoking_conflict: 1 if one is strict non-smoker and other smokes, else 0
smoking_conflict = np.random.choice([0, 1], size=n_pairs, p=[0.75, 0.25])

# 5. diet_compatibility: 1.0 (same diet / flexible), 0.5 (different but tolerant), 0.0 (strict mismatch)
diet_compat = np.random.choice([1.0, 0.7, 0.3], size=n_pairs, p=[0.55, 0.30, 0.15])

# 6. budget_similarity: ratio of overlapping budget (1.0 = identical budget, 0.0 = severe divergence)
budget_similarity = np.random.uniform(0.3, 1.0, size=n_pairs)

# 7. location_proximity: 1.0 (same campus quadrant), 0.5 (adjacent metro stop), 0.1 (far)
location_proximity = np.random.choice([1.0, 0.7, 0.2], size=n_pairs, p=[0.45, 0.35, 0.20])

# 8. university_match: 1 if attending same university/college, else 0
university_match = np.random.choice([0, 1], size=n_pairs, p=[0.40, 0.60])

# Compute Target Compatibility Score (0 - 100%) with realistic student synergy weights
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

# Add small natural behavioral noise
score += np.random.normal(0, 1.5, size=n_pairs)

# Clamp score between 35% and 98%
target_score = np.clip(score, 35, 98)
target_score = np.round(target_score, 1)

# Assemble DataFrame
features_df = pd.DataFrame({
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
features_df.to_csv(dataset_csv_path, index=False)
print(f"[1/4] Generated Roommate Compatibility Training Dataset ({n_pairs} pairs)")
print(f"      Saved to: {dataset_csv_path}")

feature_cols = [
    'sleep_delta', 'cleanliness_delta', 'noise_study_delta',
    'smoking_conflict', 'diet_compat', 'budget_similarity',
    'location_proximity', 'university_match'
]

X = features_df[feature_cols].values
y = features_df['compatibility_score'].values

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
print(f"[2/4] Train size: {len(X_train)} | Test size: {len(X_test)}")

# 3. Train Neural Network (MLP Regressor)
print("[3/4] Training Multi-Layer Perceptron (MLP) Compatibility Model...")
mlp_model = MLPRegressor(
    hidden_layer_sizes=(64, 32),
    activation='relu',
    solver='adam',
    max_iter=400,
    random_state=42,
    early_stopping=True,
    validation_fraction=0.1
)
mlp_model.fit(X_train, y_train)

# 4. Evaluation
y_pred = mlp_model.predict(X_test)
r2 = r2_score(y_test, y_pred)
mae = mean_absolute_error(y_test, y_pred)

print("----------------------------------------------------------")
print("  Roommate Neural Matcher Evaluation Results")
print("----------------------------------------------------------")
print(f"  * R^2 Score (Accuracy):   {r2 * 100:.2f}% (R^2 = {r2:.4f})")
print(f"  * Mean Absolute Error:   {mae:.2f}% score deviation")
print("----------------------------------------------------------")

# Save trained model artifact
model_artifact = {
    "model": mlp_model,
    "feature_columns": feature_cols,
    "version": "1.0.0-neural-roommate-matcher"
}

model_save_path = os.path.join(os.path.dirname(__file__), 'roommate_matcher.pkl')
joblib.dump(model_artifact, model_save_path)
print(f"[4/4] Successfully serialized model to: {model_save_path}")
print("Training process finished successfully.")

