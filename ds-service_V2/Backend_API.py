def Neutral_Definition(forecast: int, model, X_emb):
    proba = model.predict_proba(X_emb)[0]

    sorted_idx = proba.argsort()[::-1]
    top1 = float(proba[sorted_idx[0]])
    top2 = float(proba[sorted_idx[1]])

    threshold = 0.60
    delta = 0.20

    #Decide neutro por incerteza
    if top1 < threshold or (top1 - top2) < delta:
        return "Neutral", top1, 2  # label, score, label_id

    #Caso de certeza, usa forecast do modelo
    if forecast == 1:
        return "Positive", top1, 1
    else:
        return "Negative", top1, 0
