from fastapi import FastAPI
from pydantic import BaseModel
import pickle
from pathlib import Path

from Data_Tokening import embedding_text
from Neutral_Class import Neutral_Definition
from Translator import translate_commentary

app = FastAPI(title="DS Sentiment Service V2")

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "Tuning_Model.pkl"

with open(MODEL_PATH, "rb") as f:
    model = pickle.load(f)

class PredictRequest(BaseModel):
    text: str

class PredictResponse(BaseModel):
    label: str
    score: float
    label_id: int
    translated: str

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    translated = translate_commentary(req.text)
    X_emb = embedding_text([translated])

    forecast = int(model.predict(X_emb)[0])

    label, score, label_id = Neutral_Definition(
        forecast, model, X_emb
    )

    return {
        "label": label,
        "score": float(score),
        "label_id": int(label_id),
        "translated": translated
    }
