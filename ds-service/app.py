from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
import torch
import pickle
from pathlib import Path
from transformers import RobertaTokenizer, RobertaModel

app = FastAPI(title="DS Sentiment Service")

MODEL_NAME = "roberta-base"
BASE_DIR = Path(__file__).resolve().parent

# carrega modelo SVC
with open(BASE_DIR / "Best_SVC.pkl", "rb") as f:
    svc = pickle.load(f)

# carrega roberta
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
tokenizer = RobertaTokenizer.from_pretrained(MODEL_NAME)
bert_model = RobertaModel.from_pretrained(MODEL_NAME).to(device)
bert_model.eval()

class PredictRequest(BaseModel):
    text: str

class PredictResponse(BaseModel):
    label: str          # "Positive" / "Negative"
    score: float        # probabilidade da classe positiva
    label_id: int       # 1 ou 0

def embed_one(text: str) -> np.ndarray:
    encoded = tokenizer(
        [text],
        padding=True,
        truncation=True,
        max_length=128,
        return_tensors="pt"
    )
    encoded = {k: v.to(device) for k, v in encoded.items()}
    with torch.no_grad():
        out = bert_model(**encoded)
        emb = out.last_hidden_state[:, 0, :]  # CLS
    return emb.cpu().numpy()  # shape (1, 768)

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    x = embed_one(req.text)
    proba_pos = float(svc.predict_proba(x)[0][1])
    pred = int(svc.predict(x)[0])  # 1=pos, 0=neg
    label = "Positive" if pred == 1 else "Negative"
    return {"label": label, "score": proba_pos, "label_id": pred}
