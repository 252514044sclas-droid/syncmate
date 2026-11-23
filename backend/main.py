import os
import sys

# Ensure parent directory is in sys.path
parent_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if parent_dir not in sys.path:
    sys.path.insert(0, parent_dir)

from main import app

if __name__ == "__main__":
    import uvicorn
    print("[*] Starting SyncMate FastAPI AI Backend on 0.0.0.0:8000...")
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=False)
