import requests
from datetime import datetime
import uuid

def get_exchange_rate(from_currency: str, to_currency: str):
    url = f"https://economia.awesomeapi.com.br/last/{from_currency}-{to_currency}"
    response = requests.get(url)
    data = response.json()
    pair = f"{from_currency}{to_currency}".upper()

    if pair not in data:
        raise Exception("Invalid currency pair")

    rate = data[pair]
    return {
        "sell": float(rate["ask"]),
        "buy": float(rate["bid"]),
        "date": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "id-account": str(uuid.uuid4())
    }
