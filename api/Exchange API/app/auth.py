import os
from fastapi import HTTPException
from dotenv import load_dotenv

load_dotenv()

VALID_TOKEN = os.getenv("API_TOKEN")

def verify_token(token: str):
    if not token or token != f"Bearer {VALID_TOKEN}":
        raise HTTPException(status_code=401, detail="Unauthorized")
