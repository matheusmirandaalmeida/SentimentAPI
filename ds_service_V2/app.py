from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
import torch
import pickle
from pathlib import Path

from transformers import RobertaTokenizer, RobertaModel
from deep_translator import GoogleTranslator

# Configuração da aplicação
app = FastAPI(title="DS Sentiment Service (Positive / Negative / Neutral)")

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "Tuning_Model.pkl"

MODEL_NAME = "roberta-base"
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# Carregamento do tokenizer e modelo BERT (RoBERTa)
tokenizer = RobertaTokenizer.from_pretrained(MODEL_NAME)
bert_model = RobertaModel.from_pretrained(MODEL_NAME).to(device)
bert_model.eval()

# Carregamento do modelo treinado (sklearn)
with open(MODEL_PATH, "rb") as f:
    model = pickle.load(f)

# Contratos HTTP
class PredictRequest(BaseModel):
    # Mantém o campo "text" para compatibilidade com o backend Java
    text: str


class PredictResponse(BaseModel):
    label: str       # "Positive", "Negative" ou "Neutral"
    score: float     # confiança do top-1
    label_id: int    # 1=Positive | 0=Negative | 2=Neutral
    translated: str  # texto efetivamente analisado

# Tradução automática (fallback seguro)
def translate_commentary(input_text: str) -> str:
    try:
        translator = GoogleTranslator(source="auto", target="en")
        return translator.translate(input_text)
    except Exception:
        # Se a tradução falhar, segue com o texto original
        return input_text

# Geração de embeddings usando RoBERTa
def embedding_text(texts: list[str]) -> np.ndarray:
    all_embeddings = []

    for i in range(0, len(texts), 32):
        batch_texts = texts[i:i + 32]

        encoded_input = tokenizer(
            batch_texts,
            padding=True,
            truncation=True,
            max_length=128,
            return_tensors="pt"
        )
        encoded_input = {k: v.to(device) for k, v in encoded_input.items()}

        with torch.no_grad():
            outputs = bert_model(**encoded_input)
            # Usa o token CLS
            batch_embeddings = outputs.last_hidden_state[:, 0, :]

        all_embeddings.append(batch_embeddings.cpu().numpy())

    return np.vstack(all_embeddings)

# Regra de decisão com sentimento neutro
def neutral_definition(
        forecast: int,
        model,
        X_emb: np.ndarray
) -> tuple[str, float, int]:
    """
    Retorna:
      label     -> "Positive" | "Negative" | "Neutral"
      score     -> confiança
      label_id  -> 1 | 0 | 2
    """

    # Fallback caso o modelo não possua predict_proba
    if not hasattr(model, "predict_proba"):
        if forecast == 1:
            return "Positive", 1.0, 1
        return "Negative", 1.0, 0

    proba = model.predict_proba(X_emb)[0]  # ex: [neg, pos]
    sorted_idx = proba.argsort()[::-1]

    top1 = float(proba[sorted_idx[0]])
    top2 = float(proba[sorted_idx[1]])

    threshold = 0.60
    delta = 0.20

    # Regra de neutralidade
    if top1 < threshold or (top1 - top2) < delta:
        return "Neutral", top1, 2

    if int(forecast) == 1:
        return "Positive", top1, 1

    return "Negative", top1, 0

# Endpoints
@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    # Tradução
    translated = translate_commentary(req.text)

    # Embedding
    X_emb = embedding_text([translated])

    # Previsão base (0 ou 1)
    forecast = int(model.predict(X_emb)[0])

    # Decisão final (inclui neutro)
    label, score, label_id = neutral_definition(forecast, model, X_emb)

    return {
        "label": label,
        "score": float(score),
        "label_id": int(label_id),
        "translated": translated
    }