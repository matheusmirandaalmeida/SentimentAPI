import numpy as np

def Neutral_Definition(forecast: int, model, embedding) -> tuple[str, float, int]:

    proba = model.predict_proba(embedding)[0]

    # Ordena probabilidades
    sorted_idx = proba.argsort()[::-1]
    top1 = float(proba[sorted_idx[0]])
    top2 = float(proba[sorted_idx[1]])

    # Limiares para decisão
    CONFIDENCE_THRESHOLD = 0.60
    MARGIN_THRESHOLD = 0.20

    is_low_confidence = top1 < CONFIDENCE_THRESHOLD
    is_small_margin = (top1 - top2) < MARGIN_THRESHOLD

    if is_low_confidence or is_small_margin:
        return "Neutral", top1, 2

    if forecast == 1:
        return "Positive", top1, 1
    elif forecast == 0:
        return "Negative", top1, 0
    else:
        return "Neutral", top1, 2