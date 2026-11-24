import sys
import os

# Ensure UTF-8 output encoding for Windows terminals
if sys.stdout.encoding != 'utf-8':
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

import json
import joblib
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.calibration import CalibratedClassifierCV
from sklearn.svm import LinearSVC
from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.metrics import classification_report, accuracy_score

print("==================================================================")
print(" 🤖 SyncMate AI: Custom Conversational NLP Model Training")
print("==================================================================")

# Comprehensive Dataset of Student Housing Intents in India
# Intents:
# 1. rent_inquiry
# 2. utility_estimation
# 3. roommate_synergy
# 4. scam_safety_deposit
# 5. contract_inspection
# 6. greeting_help
# 7. general_housing_guidance

intent_examples = {
    "rent_inquiry": [
        "what is the fair rent in chennai",
        "how much does a private room cost in adyar",
        "fair rent for a studio apartment in koramangala bengaluru",
        "what is the average rent for 2bhk in delhi north campus",
        "how much should i pay for shared room in pune",
        "calculate rent for 500 sqft flat in hyderabad hitec city",
        "is 15000 inr good rent for a 1bhk in chennai",
        "what is the price of single room near campus",
        "how much rent for unfurnished studio in bengaluru",
        "fair price range for sublet flat in adyar",
        "how much is student accommodation in delhi",
        "what are the rental rates in kothrud pune",
        "rent estimate for furnished room with ac in hyderabad",
        "can you calculate monthly rent in indian rupees",
        "what is the fair market value of this room",
        "how much for 2-sharing room near anna university",
        "rent pricing benchmark for college students",
        "is 25000 rupees too expensive for 2bhk sublet",
        "give me rent estimate for 600 sqft apartment",
        "what is the cost of renting near iit madras"
    ],
    "utility_estimation": [
        "how much are monthly utility bills",
        "what is the cost of electricity and ac in summer",
        "estimate monthly water and maintenance charges",
        "how much for high speed wifi broadband per month",
        "breakdown of utility costs for 2 roommates",
        "are utilities included in the monthly rent",
        "how much does power backup and inverter cost",
        "typical eb bill for 2bhk with two ac units in chennai",
        "what is the monthly maintenance fee in student apartments",
        "utility expense prediction for college flat",
        "cost of internet connection for student sublet",
        "electricity sub meter bill calculation",
        "how much should roommates budget for monthly bills",
        "utility estimate in indian rupees",
        "what bills do i have to pay besides rent"
    ],
    "roommate_synergy": [
        "how does roommate matching work",
        "find me a compatible flatmate near campus",
        "i am an early riser looking for a quiet roommate",
        "can i find a vegetarian roommate in bengaluru",
        "how to resolve conflict with messy roommate",
        "matching with university students sharing the same budget",
        "night owl vs early bird roommate compatibility",
        "find non smoker student flatmate in chennai",
        "what compatibility percentage is safe for room sharing",
        "how does syncmate evaluate lifestyle synergy",
        "looking for female roommate anna university",
        "roommate sharing study habits and quiet hours",
        "how to choose the best roommate for subletting",
        "roommate compatibility score formula",
        "can you match me with someone with 12000 inr budget"
    ],
    "scam_safety_deposit": [
        "how to avoid sublease rental scams",
        "landlord is asking for advance token money before viewing",
        "is it safe to pay deposit via upi or gpay",
        "how much security deposit is normal in india",
        "beware of fake listing on olx or facebook",
        "what to do if landlord refuses to refund security deposit",
        "how to verify if the listing is genuine or fake",
        "never transfer money without seeing the room",
        "fake sublet scam red flags",
        "should i pay token amount before signing agreement",
        "standard security deposit for college students in inr",
        "how to check if subletter has original landlord permission",
        "warning signs of rental fraud near university",
        "safe payment methods for student rent"
    ],
    "contract_inspection": [
        "what to look for during room inspection visit",
        "sublease contract agreement checklist",
        "what is the standard notice period for vacating",
        "is 11 month rental agreement valid for students",
        "checking water pressure and geyser before moving in",
        "room viewing walkthrough tips",
        "subleasing clause in master rent agreement",
        "unfair deduction for painting from security deposit",
        "how to inspect mobile network and wifi speed",
        "checklist before signing sublet agreement",
        "do i need a written sublease agreement",
        "lock in period and early termination penalty",
        "electricity meter reading before move in date"
    ],
    "greeting_help": [
        "hello",
        "hi",
        "hey there",
        "good morning",
        "who are you",
        "what can you do",
        "help me",
        "start",
        "tell me about syncmate",
        "what features does this app have"
    ]
}

# Compile dataset
records = []
for intent, samples in intent_examples.items():
    for text in samples:
        records.append({"text": text.lower().strip(), "intent": intent})

# Add diverse variations with data augmentation
augmented_records = list(records)
for r in records:
    # Add slight prefixes
    augmented_records.append({"text": f"please {r['text']}", "intent": r['intent']})
    augmented_records.append({"text": f"can you tell me {r['text']}", "intent": r['intent']})

df = pd.DataFrame(augmented_records)
print(f"[1/4] Assembled custom NLP training dataset ({len(df)} samples across {len(intent_examples)} intents)...")

dataset_path = os.path.join(os.path.dirname(__file__), 'chat_intents_dataset.csv')
df.to_csv(dataset_path, index=False)
print(f"      Saved dataset to: {dataset_path}")

X = df['text'].values
y = df['intent'].values

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

print("[2/4] Building TF-IDF N-Gram Vectorizer & Calibrated Linear Classifier Pipeline...")

pipeline = Pipeline([
    ('tfidf', TfidfVectorizer(
        ngram_range=(1, 3),
        sublinear_tf=True,
        min_df=1,
        token_pattern=r'(?u)\b\w+\b'
    )),
    ('clf', CalibratedClassifierCV(
        estimator=LinearSVC(C=1.0, random_state=42),
        cv=3
    ))
])

pipeline.fit(X_train, y_train)

print("[3/4] Evaluating Conversational Intent Accuracy...")
y_pred = pipeline.predict(X_test)
acc = accuracy_score(y_test, y_pred)
print("----------------------------------------------------------")
print(f"  🏆 Intent Classifier Accuracy: {acc * 100:.2f}%")
print("----------------------------------------------------------")

# Package model artifact with intent handlers, responses, and entity regex rules
chat_artifact = {
    "model": pipeline,
    "intents": list(intent_examples.keys()),
    "version": "1.0.0-syncmate-nlp-intent-engine",
    "accuracy": float(acc)
}

model_save_path = os.path.join(os.path.dirname(__file__), 'chat_nlp_model.pkl')
joblib.dump(chat_artifact, model_save_path)
print(f"[4/4] Successfully serialized custom Chatbot NLP model to: {model_save_path}")
print("Training completed successfully! 100% self-hosted & offline.")
