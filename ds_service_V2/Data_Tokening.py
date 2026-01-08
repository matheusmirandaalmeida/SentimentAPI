import os
import numpy as np
import torch
from transformers import RobertaTokenizer, RobertaModel

MODEL_NAME = "roberta-base"
MAX_LENGTH = 128
BATCH_SIZE = 32

# Escolhe device automaticamente
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# Carrega tokenizer e modelo uma vez (startup do serviço)
tokenizer = RobertaTokenizer.from_pretrained(MODEL_NAME)
bert_model = RobertaModel.from_pretrained(MODEL_NAME).to(DEVICE)
bert_model.eval()


def embedding_text(texts):
    """
    Recebe uma lista[str] e retorna np.ndarray shape (n, 768)
    """
    if texts is None:
        raise ValueError("texts não pode ser None")
    if len(texts) == 0:
        return np.empty((0, 768), dtype=np.float32)

    # Garante que tudo é string
    texts = ["" if t is None else str(t) for t in texts]

    all_embeddings = []

    for i in range(0, len(texts), BATCH_SIZE):
        batch_texts = texts[i:i + BATCH_SIZE]

        encoded = tokenizer(
            batch_texts,
            padding=True,
            truncation=True,
            max_length=MAX_LENGTH,
            return_tensors="pt"
        )

        # manda pro device
        encoded = {k: v.to(DEVICE) for k, v in encoded.items()}

        # inferência mais eficiente
        with torch.inference_mode():
            outputs = bert_model(**encoded)
            cls_emb = outputs.last_hidden_state[:, 0, :]  # (batch, 768)

        all_embeddings.append(cls_emb.cpu().numpy().astype(np.float32))

    return np.vstack(all_embeddings)


def get_or_create_embeddings(texts, emb_path: str):
    if os.path.exists(emb_path):
        print(f"Carregando embeddings X: {emb_path}")
        return np.load(emb_path)

    print(f"Gerando embeddings X: {emb_path}")
    X_emb = np.array(embedding_text(list(texts)))
    np.save(emb_path, X_emb)
    return X_emb


def save_embeddings(X, Y, x_path, y_path):
    np.save(x_path, X)
    np.save(y_path, Y)


def load_embeddings(x_path, y_path):
    X = np.load(x_path)
    Y = np.load(y_path)
    return X, Y
