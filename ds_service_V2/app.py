from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pickle
from pathlib import Path
import numpy as np
import warnings
from fastapi.middleware.cors import CORSMiddleware

# Suprimir warnings
warnings.filterwarnings("ignore")

app = FastAPI(title="DS Sentiment Service V2")

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Modelo placeholder
class SimpleModel:
    def predict(self, X):
        return np.random.randint(0, 2, size=len(X))
    def predict_proba(self, X):
        return np.random.rand(len(X), 3)

# Carregar modelo
BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "Tuning_Model.pkl"

if MODEL_PATH.exists():
    try:
        with open(MODEL_PATH, "rb") as f:
            model = pickle.load(f)
        print("✅ Modelo carregado")
    except Exception as e:
        print(f"❌ Erro ao carregar modelo: {e}")
        model = SimpleModel()
else:
    model = SimpleModel()
    print("⚠️  Modelo não encontrado, usando placeholder")

# Modelo de requisição
class PredictRequest(BaseModel):
    text: str

class PredictResponse(BaseModel):
    label: str
    score: float
    label_id: int
    translated: str

@app.get("/")
def root():
    return {
        "service": "DS Sentiment Service V2",
        "endpoints": {
            "health": "/health (GET)",
            "predict": "/predict (POST)",
            "docs": "/docs"
        }
    }

@app.get("/health")
def health():
    return {
        "status": "ok",
        "model_loaded": not isinstance(model, SimpleModel),
        "model_path": str(MODEL_PATH)
    }

@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    try:
        print(f"📨 Recebido: {req.text}")

        # Simulação de tradução
        translated = req.text  # Não traduz para teste

        # Simulação de embedding
        X_emb = np.random.randn(1, 768).astype(np.float32)

        # Previsão
        forecast = int(model.predict(X_emb)[0])

        # Lógica de neutralidade simulada
        proba = model.predict_proba(X_emb)[0]
        sorted_idx = proba.argsort()[::-1]
        top1 = float(proba[sorted_idx[0]])
        top2 = float(proba[sorted_idx[1]])

        threshold = 0.60
        delta = 0.20

        if top1 < threshold or (top1 - top2) < delta:
            label, score, label_id = "Neutral", top1, 2
        elif forecast == 1:
            label, score, label_id = "Positive", top1, 1
        else:
            label, score, label_id = "Negative", top1, 0

        return PredictResponse(
            label=label,
            score=score,
            label_id=label_id,
            translated=translated
        )

    except Exception as e:
        print(f" Erro: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)