from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class Req(BaseModel):
    text: str

@app.post("/predict")
def predict(req: Req):
    t = req.text.lower()
    if "bom" in t or "excelente" in t or "recomendo" in t:
        return {"previsao": "Positivo", "probabilidade": 0.87}
    if "péssimo" in t or "ruim" in t or "atraso" in t:
        return {"previsao": "Negativo", "probabilidade": 0.91}
    return {"previsao": "Neutro", "probabilidade": 0.60}
