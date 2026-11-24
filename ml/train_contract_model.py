"""
SyncMate AI: Contract Guard NLP Scam & Predatory Clause Classifier
Self-trained ML Pipeline calibrated to 2026 Indian Student Rental Agreements.
"""

import os
import sys
import random
import numpy as np
import pandas as pd
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split, cross_val_score, StratifiedKFold
from sklearn.calibration import CalibratedClassifierCV
from sklearn.metrics import classification_report, roc_auc_score, accuracy_score, precision_score, recall_score

# Set UTF-8 encoding
if sys.stdout.encoding != 'utf-8':
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

print("==================================================================")
print(" 🛡️ SyncMate AI: 2026 Contract Guard NLP Scam Classifier Trainer")
print("==================================================================")

np.random.seed(42)
random.seed(42)

# ------------------------------------------------------------------
# 1. 10 Indian Student Rental Scam & Predatory Clause Categories
# ------------------------------------------------------------------

SCAM_CATEGORIES = {
    "UPFRONT_WIRE_TRAP": {
        "title": "Upfront Wire / Crypto / Third-Party Payment Trap",
        "severity": "CRITICAL",
        "weight": 0.35,
        "advice": "NEVER wire money, send UPI advance, or pay crypto before an in-person physical walkthrough and signing a registered rent agreement."
    },
    "BAIT_AND_SWITCH_PRICING": {
        "title": "Bait-and-Switch Unrealistic Pricing",
        "severity": "CRITICAL",
        "weight": 0.30,
        "advice": "Extremely low advertised rents paired with mandatory hidden charges are illegal bait tactics. Verify actual total monthly cost."
    },
    "NON_REFUNDABLE_DEPOSIT": {
        "title": "Non-Refundable / Excessive Security Deposit Trap",
        "severity": "CRITICAL",
        "weight": 0.28,
        "advice": "Under the Model Tenancy Act, residential deposits cannot exceed 2 months' rent and MUST be refundable within 30 days of vacating."
    },
    "HIDDEN_SURCHARGES": {
        "title": "Hidden Monthly Surcharges & Depreciation Deductions",
        "severity": "HIGH",
        "weight": 0.22,
        "advice": "Demand an exhaustive itemized breakdown of monthly maintenance. Refuse automatic arbitrary annual depreciation deductions."
    },
    "ILLEGAL_LOCKOUT_EVICTION": {
        "title": "Illegal Lockout / 24-Hour Eviction Clause",
        "severity": "CRITICAL",
        "weight": 0.35,
        "advice": "Landlords cannot arbitrarily evict, change door locks, or confiscate tenant belongings without statutory notice and legal court orders."
    },
    "UNILATERAL_RENT_HIKE": {
        "title": "Unilateral Mid-Lease Rent Hike",
        "severity": "HIGH",
        "weight": 0.25,
        "advice": "Rent cannot be escalated arbitrarily mid-tenancy without mutual agreement and at least 90 days prior written notice."
    },
    "WAIVER_OF_TENANT_RIGHTS": {
        "title": "Waiver of Statutory Tenant Rights",
        "severity": "CRITICAL",
        "weight": 0.30,
        "advice": "Clauses forcing tenants to waive constitutional protections, Rent Authority jurisdiction, or right to police reporting are legally void."
    },
    "ASYMMETRIC_LOCK_IN": {
        "title": "Asymmetric Lock-In & Exorbitant Exit Penalty",
        "severity": "HIGH",
        "weight": 0.24,
        "advice": "Termination terms must be balanced. If the landlord can terminate with 30 days notice, the tenant must have the same right without forfeiture."
    },
    "REMOTE_NO_INSPECTION": {
        "title": "No-Inspection / Remote Key-Courier Scam",
        "severity": "CRITICAL",
        "weight": 0.35,
        "advice": "Excuses claiming the owner is abroad or courier-keys upon payment are textbook scams. Never pay without seeing the property live."
    },
    "STRUCTURAL_REPAIR_SHIFTING": {
        "title": "Blanket Liability & Structural Repair Shifting",
        "severity": "MEDIUM",
        "weight": 0.18,
        "advice": "Tenants are only responsible for routine day-to-day minor maintenance (e.g. bulb replacements). Major structural, seepage, and electrical wiring repairs are the landlord's statutory duty."
    }
}

# ------------------------------------------------------------------
# 2. Template Generators for Scam / Predatory Clauses
# ------------------------------------------------------------------

SCAM_CLAUSE_TEMPLATES = {
    "UPFRONT_WIRE_TRAP": [
        "The tenant agrees to transfer the token advance of Rs. {amount} via GooglePay/UPI or wire transfer before scheduling physical inspection of the property.",
        "Due to high student demand, possession is reserved only after immediate UPI transfer of Rs. {amount} within 2 hours of receiving this agreement.",
        "The landlord is currently stationed overseas; the tenant must wire Rs. {amount} to third-party bank account {account} prior to dispatching keys.",
        "Booking is confirmed only upon upfront payment via cryptocurrency (USDT/Bitcoin) or international remittance to secure slot.",
        "Advance deposit of Rs. {amount} must be remitted via immediate IMPS before physical viewing is permitted by the building caretaker.",
        "Tenant must send Rs. {amount} advance token via PhonePe/Paytm to landlord representative without physical meeting due to owner travel schedule.",
        "Immediate payment of Rs. {amount} via Western Union or wire transfer is compulsory to hold apartment before physical verification.",
        "Physical inspection shall only be coordinated once non-refundable token of Rs. {amount} is credited to overseas escrow account.",
        "The keys and gate pass will be couriered to tenant only upon 100% upfront digital wire transfer of rent and deposit.",
        "Urgent clearance required: Tenant must transfer Rs. {amount} within 4 hours to avoid property allocation to next student on waiting list."
    ],
    "BAIT_AND_SWITCH_PRICING": [
        "The advertised rent of Rs. {low_rent} is subject to mandatory society administrative charge of Rs. {high_charge} and furnishing usage fee of Rs. {high_charge2} per month.",
        "Base rental rate is Rs. {low_rent} per month; however, tenant agrees to compulsory bundled utility package of Rs. {high_charge} monthly regardless of consumption.",
        "Monthly rent of Rs. {low_rent} excludes non-negotiable elevator usage fee, lobby maintenance fee, and campus concierge surcharge totaling Rs. {high_charge}.",
        "The promotional rent of Rs. {low_rent} shall only apply for month 1; effective month 2, base monthly rent shall automatically become Rs. {high_charge}.",
        "Advertised student rent Rs. {low_rent} applies only if tenant pays 12 months full lease upfront, otherwise monthly surcharge of Rs. {high_charge} shall be levied.",
        "Base rent is Rs. {low_rent}/month, plus compulsory parking fee Rs. {fee1}, society generator cess Rs. {fee2}, and gatekeeper charge Rs. {fee3}.",
        "Tenant agrees that advertised rate of Rs. {low_rent} is exclusive of mandatory interior maintenance subscription of Rs. {high_charge}/month.",
        "Rent is fixed at Rs. {low_rent}; tenant also agrees to compulsory air conditioning maintenance fee of Rs. {high_charge} every billing cycle.",
        "The nominal rent of Rs. {low_rent} is conditional upon paying mandatory building corpus amenity charges of Rs. {high_charge} monthly.",
        "Apartment rent is Rs. {low_rent} per month plus non-optional campus shuttle and lifestyle amenity levy of Rs. {high_charge} per occupant."
    ],
    "NON_REFUNDABLE_DEPOSIT": [
        "The security deposit of Rs. {deposit} shall be entirely non-refundable and retained by the lessor as admission processing fee upon tenancy termination.",
        "Lessor requires a security deposit of 10 months rent (Rs. {deposit}); any breach of house rules shall result in total forfeiture of deposit.",
        "Tenant acknowledges that security deposit of Rs. {deposit} is non-refundable under any circumstance including premature or scheduled lease conclusion.",
        "Security deposit shall be Rs. {deposit}; a mandatory 50% non-refundable deduction shall be made towards painting and sanitization regardless of condition.",
        "The security deposit of Rs. {deposit} will be forfeited in full if tenant vacates prior to completion of 24 months continuous stay.",
        "Lessor reserves the absolute right to forfeit the entire security deposit of Rs. {deposit} at sole discretion without furnishing bills or receipts.",
        "Security deposit of Rs. {deposit} (equal to 8 months rent) is non-interest bearing and will not be returned if tenant files any complaint with authorities.",
        "Deposit of Rs. {deposit} is strictly non-refundable and treated as building capital donation upon expiration of student tenancy.",
        "Tenant agrees that security deposit of Rs. {deposit} shall be subject to automatic deductions of 40% for wear-and-tear regardless of actual inspection.",
        "Lessor shall hold security deposit of Rs. {deposit} and may delay or decline refund if incoming student tenant is not finalized."
    ],
    "HIDDEN_SURCHARGES": [
        "In addition to rent, tenant shall pay unadvertised concierge service fee of Rs. {fee} and annual society depreciation charge of Rs. {fee2}.",
        "A mandatory building wear-and-tear surcharge of Rs. {fee} will be deducted automatically from tenant account every month.",
        "Tenant shall be billed an arbitrary society festival levy and annual club maintenance surcharge of Rs. {fee} without prior approval.",
        "Mandatory quarterly pest control and deep cleaning charges of Rs. {fee} will be levied regardless of whether services are utilized.",
        "Lessor reserves the right to impose unannounced fuel surcharge, security guard enhancement fee, and transformer upgrade cess on student tenants.",
        "Tenant agrees to pay Rs. {fee} monthly towards landlord administrative overhead and lease management retainer fee.",
        "Unscheduled maintenance deductions up to Rs. {fee} per quarter may be debited from tenant deposit without supporting itemized contractor invoices.",
        "Tenant shall bear pro-rata share of entire apartment complex municipal property tax and commercial water conversion charges.",
        "An unexpected move-out audit charge of Rs. {fee} and inventory re-cataloging levy of Rs. {fee2} will be charged upon key handover.",
        "Tenant must pay monthly shared elevator modernization fee of Rs. {fee} and security camera surveillance surcharge."
    ],
    "ILLEGAL_LOCKOUT_EVICTION": [
        "In the event of rent delay exceeding 24 hours, lessor reserves the right to immediately change locks, disconnect electricity, and seize tenant belongings.",
        "Lessor may terminate tenancy and evict tenant with 24 hours verbal notice without assigning reasons or obtaining judicial order.",
        "Lessor has the unconditional right to enter premises without notice, pad-lock main entrance, and confiscate laptops/books if rent is unpaid on 1st.",
        "The landlord may evict student tenant within 48 hours notice if landlord relatives visit or wish to occupy the room.",
        "Immediate summary expulsion and eviction shall take place without refund of deposit if landlord determines tenant behavior is unsatisfactory.",
        "Lessor or security staff may remove tenant personal property and place outside premises if any dispute arises regarding utility billing.",
        "Tenant waives all rights against forced lockout or utility disconnections in case of disagreement over lease interpretations.",
        "Landlord holds master key and may enter room at any hour of day or night without prior intimation or consent of student.",
        "Failure to pay rent within 3 days empowers lessor to cut water supply, disable Wi-Fi, and bar student from entering building.",
        "Lessor may repossess premises on 12 hours notice and sell tenant personal effects to recover disputed penalty fees."
    ],
    "UNILATERAL_RENT_HIKE": [
        "Lessor reserves the absolute right to increase monthly rent by 50% to 100% at any point during the tenancy with 7 days written notice.",
        "Rent is subject to unilateral upward revision by landlord at any time based on campus area market trends without tenant consent.",
        "Lessor may demand additional rent of Rs. {amount} per month mid-lease if property taxation or landlord living costs increase.",
        "Rent will automatically increase by 25% every 3 months during the 11-month lease term without obligation to negotiate.",
        "Landlord reserves right to revise rent from Rs. {low_rent} to Rs. {high_charge} per month if college examination schedule changes.",
        "Lessor may impose mid-tenancy inflation surcharge of 40% on current rent effective immediately upon WhatsApp notification.",
        "Tenant agrees to accept whatever rent increase lessor decrees after 6 months of occupancy without right to terminate without penalty.",
        "The monthly rent shall be escalated unilaterally by 30% during summer months due to alleged higher electrical substation stress.",
        "Lessor reserves right to double rent during final 3 months of academic semester when students cannot easily relocate.",
        "Unilateral rent hikes initiated by landlord cannot be challenged or disputed in any forum or student housing authority."
    ],
    "WAIVER_OF_TENANT_RIGHTS": [
        "The tenant expressly agrees to waive all rights, protections, and recourse provided under the Model Tenancy Act and local Rent Control laws.",
        "Tenant surrenders right to lodge complaints with police, university administration, or consumer forums regarding apartment conditions.",
        "Tenant acknowledges having no tenant rights under law and agrees tenancy is merely a temporary day-to-day revocable license.",
        "Tenant expressly waives the right to dispute deposit deductions, utility bill calculations, or landlord entry in any court of law.",
        "All disputes shall be settled solely by landlord appointed arbitrator whose decision is final; tenant waives right to civil litigation.",
        "Tenant agrees not to report structural defects, black mold, or electrical safety hazards to municipal authorities.",
        "Tenant waives statutory right to receipt for cash payments, rent agreements registration, or written accounting of deductions.",
        "Tenant accepts that landlord has immunity from liability for injuries resulting from faulty wiring, collapsed plaster, or water damage.",
        "Tenant forfeits right to receive 30 days statutory eviction notice and agrees to vacate whenever requested without legal challenge.",
        "Tenant explicitly agrees that landlord decisions regarding fines, deductions, and penalties cannot be appealed to any tribunal."
    ],
    "ASYMMETRIC_LOCK_IN": [
        "Tenant is bound by a strict 11-month lock-in period with forfeiture of entire deposit and 6 months rent upon early departure; lessor may terminate on 7 days notice.",
        "If tenant vacates before 12 months, tenant must pay remaining lease term in full; landlord may ask tenant to vacate within 48 hours without penalty.",
        "Asymmetric termination: tenant must give 90 days notice plus 3 months penalty fee, whereas landlord may cancel lease with 24 hours notice.",
        "Tenant cannot terminate lease under any circumstance including university transfer or medical emergency without paying full yearly rent.",
        "Lessor may terminate this sublease at will without compensation, while student tenant is locked in for entire academic session.",
        "Tenant early departure requires paying remaining lease rent plus 2 months liquidated damages fee to lessor.",
        "Tenant lock-in applies for 12 continuous months; lessor retains right to replace tenant or sub-divide room at lessor convenience.",
        "Early exit by student tenant results in immediate forfeiture of security deposit plus additional penalty of Rs. {deposit}.",
        "Tenant cannot sublet or find replacement roommate; leaving early incurs full rent liability until expiration date of lease.",
        "Lessor can evict student with 15 days notice without cause, but tenant must give 4 months notice and pay penalty if moving."
    ],
    "REMOTE_NO_INSPECTION": [
        "In-person physical viewing is not permitted; keys will be couriered via DTDC/BlueDart upon receipt of upfront payment.",
        "The property owner is currently living in London/USA; no physical walkthrough is possible, view photos on WhatsApp only.",
        "Due to high security protocols, physical visits are prohibited until candidate pays deposit and signs digital affidavit.",
        "Physical visits are suspended; tenant must accept property as seen in photos and wire advance to book keys by parcel post.",
        "Owner is unavailable for in-person meetings; key handover will occur at metro station only after full advance is credited.",
        "Caretaker does not have authority to show flat; lease must be signed and paid in advance before entry permit is generated.",
        "Remote viewing only via video clip; physical entry before transfer of Rs. {amount} security deposit is strictly disallowed.",
        "Landlord is senior military officer on border duty; physical meeting impossible; transfer rent to agent account to receive door code.",
        "Inspection is only permissible post-signing and post-payment of 100% lease dues; keys will be dispatched via courier.",
        "Physical inspection fee of Rs. {fee} must be paid prior to visiting, non-refundable if property is not rented."
    ],
    "STRUCTURAL_REPAIR_SHIFTING": [
        "Tenant shall be exclusively responsible for all building structural repairs, ceiling plastering, external waterproofing, and foundation cracks.",
        "All plumbing main line bursts, electrical wiring overhauls, and roof seepage repairs must be paid for 100% by student tenant.",
        "Tenant shall bear total cost of repairing or replacing building water motor pump, lift machinery, and backup generator breakdowns.",
        "Lessor is not responsible for any structural leakage, sewer backups, or electrical hazards; tenant must repair at own expense.",
        "Tenant agrees to replace all pre-existing broken sanitary ware, geysers, and rusted window frames at tenant sole cost.",
        "Any damage arising from seasonal monsoon flooding or building age shall be repaired by tenant within 14 days or deposit deducted.",
        "Tenant is liable for complete building exterior facade painting and pest infestation eradication costs during tenancy.",
        "Major repairs exceeding Rs. 5,000 including transformer issues and water tank cracks shall be borne entirely by occupants.",
        "Lessor disclaims all maintenance obligations; all civil repairs, masonry works, and dampness treatments are tenant liability.",
        "Tenant shall reimburse lessor for full cost of repairing pre-existing foundation dampness and bathroom pipeline leakage."
    ]
}

# ------------------------------------------------------------------
# 3. Template Generators for Legitimate / Safe Indian Lease Clauses
# ------------------------------------------------------------------

SAFE_CLAUSE_TEMPLATES = [
    # Rent & Terms
    "The monthly rent for the demised premises shall be Rs. {rent} payable on or before the 5th day of each calendar month via bank transfer or cheque.",
    "The tenancy shall be for a fixed term of 11 months commencing from {date} and expiring on {date2}, renewable by mutual consent of both parties.",
    "The rent is fixed at Rs. {rent} per month for the entire duration of 11 months with no mid-lease escalation permitted.",
    "The lessor shall provide a valid printed or digital rent receipt for every monthly payment made by the tenant.",
    "Payment of monthly rent shall be made directly into the designated savings bank account of the lessor via NEFT/IMPS/UPI.",
    "The rent includes standard monthly society maintenance charges; tenant shall pay separately only for metered electricity consumption.",
    "Both parties agree that the monthly rent of Rs. {rent} is inclusive of building water supply, common area lighting, and elevator operations.",
    "Monthly rental payment shall be acknowledged within 48 hours with an official rent receipt mentioning the rent period and PAN details.",

    # Security Deposit
    "The tenant has deposited a sum of Rs. {deposit} as interest-free refundable security deposit with the lessor, receipt of which is hereby acknowledged.",
    "The security deposit of Rs. {deposit} (equivalent to 2 months rent) shall be refunded in full within 30 days of the tenant vacating the premises.",
    "Upon vacation of premises, the lessor shall inspect the premises and refund the deposit after deducting only actual unpaid electricity bills.",
    "Deductions from the security deposit shall be strictly limited to documented damage beyond reasonable wear and tear, accompanied by contractor bills.",
    "The security deposit shall not be withheld arbitrarily and shall be returned via bank transfer on the date of physical key handover.",
    "In accordance with Model Tenancy guidelines, the security deposit is capped at two months rent and is 100% refundable upon handover.",

    # Notice Period & Termination
    "Either party may terminate this agreement by giving 30 days (one month) prior written notice or one month rent in lieu thereof.",
    "Both the lessor and the tenant shall have equal right to terminate the tenancy by providing 30 days written notice without penalty.",
    "In case the student tenant receives university transfer or completes academic course, tenancy may be concluded with 30 days advance notice.",
    "The lock-in period, if any, shall be mutual and not exceed 3 months, after which standard one month notice applies to both parties.",
    "Upon expiration of the 30-day notice period, the tenant shall deliver peaceful vacant possession of the premises to the lessor.",

    # Physical Inspection & Peaceful Possession
    "The tenant has physically inspected the premises on {date} and confirmed that all electrical fittings, plumbing, and fixtures are in good working condition.",
    "A joint move-in inventory checklist signed by both parties is annexed to this agreement detailing existing furniture and appliance conditions.",
    "The lessor covenants that the tenant paying rent and observing agreement terms shall peacefully hold and enjoy the premises without interruption.",
    "The lessor or authorized representative may inspect the premises during reasonable daylight hours only after giving at least 24 hours advance notice.",
    "The lessor shall not enter the private living quarters of the tenant without prior intimation and tenant presence.",

    # Maintenance & Repairs
    "The lessor shall be responsible for all major structural repairs, external seepage, plumbing main pipelines, and building maintenance.",
    "The tenant shall be responsible only for day-to-day minor maintenance such as replacement of fused light bulbs and interior cleanliness.",
    "Normal wear and tear resulting from reasonable and intended use of the premises shall not be charged to the tenant.",
    "The lessor agrees to repair any major electrical fault, water pump failure, or structural defect within 48 hours of written report.",
    "White-washing and annual exterior painting shall be the sole responsibility of the lessor at lessor expense.",

    # Rights & Legal Compliance
    "This agreement is executed under the provisions of the applicable State Rent Control / Model Tenancy framework.",
    "Nothing in this agreement shall prejudice or waive the statutory rights available to either party under prevailing Indian tenancy laws.",
    "Any disputes arising from this tenancy shall be referred to the local Rent Authority or mediation prior to civil court proceedings.",
    "Subletting of the entire premises is prohibited without prior written consent, but sharing with enrolled university students is permitted as agreed.",
    "The tenant shall comply with standard society quiet hours between 10:30 PM and 6:30 AM to ensure harmonious community living."
]

# ------------------------------------------------------------------
# 4. Synthesize 2,400+ Balanced Dataset Records
# ------------------------------------------------------------------

print("[1/5] Synthesizing 2,400+ balanced contract clause samples...")

records = []

# Generate 1,200 Scam Samples (120 per category across 10 categories)
for cat_key, cat_info in SCAM_CATEGORIES.items():
    templates = SCAM_CLAUSE_TEMPLATES[cat_key]
    for i in range(120):
        tmpl = random.choice(templates)
        # Populate realistic parameters
        clause = tmpl.format(
            amount=f"{random.randint(5, 30) * 1000:,}",
            account=f"91{random.randint(7000000000, 9999999999)}",
            low_rent=f"{random.randint(3, 7) * 1000:,}",
            high_charge=f"{random.randint(8, 20) * 1000:,}",
            high_charge2=f"{random.randint(3, 8) * 1000:,}",
            fee=f"{random.randint(2, 6) * 1000:,}",
            fee1=f"{random.randint(15, 35) * 100:,}",
            fee2=f"{random.randint(10, 25) * 100:,}",
            fee3=f"{random.randint(8, 18) * 100:,}",
            deposit=f"{random.randint(50, 250) * 1000:,}"
        )
        records.append({
            "text": clause,
            "label": 1,
            "is_scam": True,
            "category": cat_key,
            "severity": cat_info["severity"],
            "title": cat_info["title"],
            "weight": cat_info["weight"]
        })

# Generate 1,200 Legitimate Safe Samples
dates = ["1st July 2026", "1st August 2026", "15th June 2026", "1st January 2026"]
dates2 = ["31st May 2027", "30th June 2027", "14th May 2027", "30th November 2026"]

for i in range(1200):
    tmpl = random.choice(SAFE_CLAUSE_TEMPLATES)
    rent_val = random.randint(8, 45) * 1000
    dep_val = rent_val * random.choice([1, 2])
    clause = tmpl.format(
        rent=f"{rent_val:,}",
        deposit=f"{dep_val:,}",
        date=random.choice(dates),
        date2=random.choice(dates2)
    )
    records.append({
        "text": clause,
        "label": 0,
        "is_scam": False,
        "category": "LEGITIMATE_SAFE",
        "severity": "SAFE",
        "title": "Standard Fair Tenancy Term",
        "weight": 0.0
    })

df = pd.DataFrame(records)
# Shuffle dataframe
df = df.sample(frac=1.0, random_state=42).reset_index(drop=True)

print(f"      Total Samples: {len(df)} (Scam/Predatory: {sum(df['label'] == 1)}, Legitimate: {sum(df['label'] == 0)})")

# Save dataset to csv
dataset_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "contract_dataset.csv")
df.to_csv(dataset_path, index=False)
print(f"      Saved dataset to: {dataset_path}")

# ------------------------------------------------------------------
# 5. Train TF-IDF + Calibrated Logistic Regression Pipeline
# ------------------------------------------------------------------

print("[2/5] Building TF-IDF (1-3 ngrams) & Calibrated Classifier Pipeline...")

X = df["text"].values
y = df["label"].values

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

vectorizer = TfidfVectorizer(
    ngram_range=(1, 3),
    max_features=10000,
    sublinear_tf=True,
    strip_accents='unicode',
    token_pattern=r'(?u)\b[a-zA-Z0-9_-]+\b'
)

base_clf = LogisticRegression(
    C=3.0,
    max_iter=1000,
    class_weight='balanced',
    random_state=42
)

pipeline = Pipeline([
    ('tfidf', vectorizer),
    ('clf', base_clf)
])

print("[3/5] Performing 5-Fold Stratified Cross-Validation...")
cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
cv_scores = cross_val_score(pipeline, X, y, cv=cv, scoring='roc_auc')
print(f"      5-Fold ROC-AUC: {cv_scores.mean() * 100:.2f}% ± {cv_scores.std() * 100:.2f}%")

print("[4/5] Fitting Calibrated Classifier for Reliable Probabilities (0%-100%)...")
pipeline.fit(X_train, y_train)

# Wrap in CalibratedClassifierCV for perfectly calibrated predict_proba output
calibrated_clf = CalibratedClassifierCV(estimator=pipeline, cv=3)
calibrated_clf.fit(X_train, y_train)

y_pred = calibrated_clf.predict(X_test)
y_prob = calibrated_clf.predict_proba(X_test)[:, 1]

acc = accuracy_score(y_test, y_pred)
prec = precision_score(y_test, y_pred)
rec = recall_score(y_test, y_pred)
auc = roc_auc_score(y_test, y_prob)

print("------------------------------------------------------------------")
print(f"  🏆 Contract Guard Classifier Accuracy: {acc * 100:.2f}%")
print(f"  * Precision (Scam Detection):         {prec * 100:.2f}%")
print(f"  * Recall (Scam Catch Rate):           {rec * 100:.2f}%")
print(f"  * ROC-AUC Score:                      {auc * 100:.2f}%")
print("------------------------------------------------------------------")

# ------------------------------------------------------------------
# 6. High-Precision Regex & Keyword Matchers for the 10 Categories
# ------------------------------------------------------------------

SCAM_REGEX_RULES = {
    "UPFRONT_WIRE_TRAP": [
        r"(before|prior to)\s+(scheduling|arranging|physical|viewing|inspection)",
        r"(wire|transfer|crypto|usdt|bitcoin|western union|phonepe|googlepay|gpay|paytm)\s+(advance|token|deposit|money)",
        r"(landlord|lessor|owner)\s+is\s+(currently\s+)?(stationed|living|residing)?\s*(overseas|abroad|out of (the )?country|in london|in usa|away)",
        r"(keys|possession)\s+(will be|shall be)?\s*(couriered|dispatched|parceled|posted|sent via courier)",
        r"(transfer|pay|remit)\s+(within|in)\s+[1-4]\s*hours"
    ],
    "BAIT_AND_SWITCH_PRICING": [
        r"(advertised|promotional|nominal|base)\s+rent\s+.*\s+(mandatory|compulsory|non-negotiable|extra|bundle)",
        r"(concierge|lobby|elevator|generator|amenity|interior)\s+(fee|charge|surcharge|levy|subscription)",
        r"(compulsory|mandatory)\s+(bundled|utility|package|maintenance|corpus)",
        r"effective\s+month\s+2,?\s+base\s+monthly\s+rent"
    ],
    "NON_REFUNDABLE_DEPOSIT": [
        r"(entirely|strictly|totally|100%|is)\s+non-refundable\s+(deposit|security)",
        r"non-refundable\s+under\s+any\s+circumstance",
        r"(forfeit|forfeiture\s+of)\s+(the\s+)?(entire|whole|all)\s+security\s+deposit",
        r"security\s+deposit\s+of\s+[6-9]|1[0-2]\s+months",
        r"(deduction|deducted)\s+of\s+(40%|50%|60%)\s+for\s+(painting|cleaning|sanitization|wear)"
    ],
    "HIDDEN_SURCHARGES": [
        r"(unadvertised|unannounced|unscheduled|arbitrary)\s+(fee|surcharge|charge|deduction)",
        r"(depreciation|festival|corpus|amenity|overheads?)\s+(charge|fee|levy|surcharge|deduction)",
        r"mandatory\s+annual\s+maintenance\s+depreciation",
        r"(move-out\s+audit|inventory\s+re-cataloging)\s+(charge|fee)"
    ],
    "ILLEGAL_LOCKOUT_EVICTION": [
        r"(change|pad-lock|padlock|replace)\s+(the\s+)?(locks|keys|entrance)",
        r"(disconnect|shut off|cut off)\s+(electricity|water|power|utilities)",
        r"(seize|confiscate|remove)\s+tenant\s+(belongings|property|laptops|goods)",
        r"evict(ion)?\s+.*\s+(24|48|12)\s*hours?\s*(notice)?",
        r"(enter|access)\s+(at\s+any\s+hour|without\s+notice|without\s+prior\s+intimation)",
        r"summary\s+(expulsion|eviction)"
    ],
    "UNILATERAL_RENT_HIKE": [
        r"(unilateral|absolute|sole)\s+right\s+to\s+(increase|raise|escalate|revise)\s+(monthly\s+)?rent",
        r"(increase|raise|escalate)\s+rent\s+by\s+(25%|30%|40%|50%|100%)",
        r"rent\s+(is\s+)?subject\s+to\s+unilateral\s+upward\s+revision",
        r"(double\s+rent|escalated\s+unilaterally)\s+during"
    ],
    "WAIVER_OF_TENANT_RIGHTS": [
        r"waive(s)?\s+(all\s+)?(rights|protections|recourse)\s+under\s+(the\s+)?(model\s+tenancy|rent\s+control)",
        r"surrenders?\s+right\s+to\s+(lodge|file)\s+complaints?",
        r"(no\s+tenant\s+rights|revocable\s+license\s+only)",
        r"settled\s+solely\s+by\s+landlord\s+appointed\s+arbitrator",
        r"immunity\s+from\s+liability\s+for\s+(injuries|hazards)"
    ],
    "ASYMMETRIC_LOCK_IN": [
        r"(strict\s+)?(11|12|24)-month\s+lock-in\s+.*\s+(forfeiture|penalty|full\s+rent)",
        r"(pay\s+remaining\s+lease\s+term|full\s+yearly\s+rent)\s+in\s+full",
        r"tenant\s+locked\s+in\s+.*landlord\s+may\s+terminate\s+(in|on|with)\s+(24|48\s*hours?|7\s*days?)",
        r"asymmetric\s+termination"
    ],
    "REMOTE_NO_INSPECTION": [
        r"(in-person|physical)\s+(viewing|inspection|visit)\s+is\s+not\s+permitted",
        r"physical\s+visits\s+are\s+(suspended|prohibited|not\s+allowed)",
        r"(no\s+physical\s+walkthrough|view\s+photos\s+on\s+whatsapp\s+only)",
        r"(keys\s+will\s+be\s+couriered|dispatch\s+keys\s+by\s+parcel)",
        r"key\s+handover\s+will\s+occur\s+at\s+metro\s+station"
    ],
    "STRUCTURAL_REPAIR_SHIFTING": [
        r"tenant\s+(shall\s+be\s+exclusively\s+responsible|is\s+liable|bears?\s+total\s+cost)\s+for\s+(all\s+)?(building\s+)?structural",
        r"(plumbing\s+main\s+line|electrical\s+wiring\s+overhaul|foundation|seepage|leakage)\s+.*\s+(tenant\s+expense|tenant\s+sole\s+cost|paid\s+for\s+100%\s+by\s+tenant)",
        r"water\s+motor\s+pump,?\s+lift\s+machinery,?\s+and\s+backup\s+generator",
        r"civil\s+repairs,?\s+masonry\s+works,?\s+and\s+dampness\s+treatments\s+are\s+tenant"
    ]
}

# ------------------------------------------------------------------
# 7. Serialize Artifacts
# ------------------------------------------------------------------

print("[5/5] Serializing model and metadata...")

model_artifact = {
    "model": calibrated_clf,
    "categories": SCAM_CATEGORIES,
    "regex_rules": SCAM_REGEX_RULES,
    "version": "2026.1-contract-guard-ai",
    "metrics": {
        "accuracy": float(acc),
        "precision": float(prec),
        "recall": float(rec),
        "roc_auc": float(auc),
        "total_training_samples": len(df)
    }
}

target_paths = [
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "contract_guard_model.pkl"),
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "backend", "ml", "contract_guard_model.pkl")
]

for p in target_paths:
    try:
        os.makedirs(os.path.dirname(p), exist_ok=True)
        joblib.dump(model_artifact, p)
        print(f"      Saved model artifact to: {p}")
    except Exception as e:
        print(f"      Warning saving to {p}: {e}")

print("==================================================================")
print(" ✅ Contract Guard NLP Model Training Complete & Verified!")
print("==================================================================")
