def Neutral_Definition(Forecast, Model, Comentary):
    proba = Model.predict_proba(Comentary)[0]
    sorted_idx = proba.argsort()[::-1]

    top1 = float(proba[sorted_idx[0]])
    top2 = float(proba[sorted_idx[1]])

    threshold = 0.60
    delta = 0.2

    if top1 < threshold or (top1 - top2) < delta:
        return "Neutro", top1, 2

    if Forecast == 0:
        return "Negativo", top1, 0
    elif Forecast == 1:
        return "Positivo", top1, 1
    else:
        return "Neutro", top1, 2
