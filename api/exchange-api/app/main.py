from fastapi import FastAPI, Depends, HTTPException, Header
from app.exchange import get_exchange_rate
from app.auth import verify_token

app = FastAPI()

@app.get("/exchange/{from_currency}/{to_currency}")
def exchange(from_currency: str, to_currency: str, authorization: str = Header(None)):
    verify_token(authorization)  # Autenticação simples
    return get_exchange_rate(from_currency, to_currency)
