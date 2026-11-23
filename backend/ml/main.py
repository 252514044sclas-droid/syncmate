import sys
import os

# Set working directory to backend/ml
ml_dir = os.path.dirname(__file__)
if ml_dir not in sys.path:
    sys.path.insert(0, ml_dir)

from app import app

if __name__ == '__main__':
    print("[*] Starting SyncMate AI Microservice via main.py on port 5000...")
    app.run(host='0.0.0.0', port=5000, debug=False)
