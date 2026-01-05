
#Função para definir a classe Neutra
def Neutral_Definition(Forecast, Model, Comentary):
    #Probabilidade de cada classe
    proba = Model.predict_proba(Comentary)[0]
    #Ordenando as classes da maior para menor
    sorted_idx = proba.argsort()[::-1]
    #Salvando em variáveis
    top1 = proba[sorted_idx[0]]
    top2 = proba[sorted_idx[1]]
    threshold = 0.60
    delta =0.2
    print("Probabilidades:", proba)
    print("Índices ordenados:", sorted_idx)
    print("Top1:", top1)
    print("Top2:", top2)

    #Vendo a probabilidade de cada classe
    for cls, p in zip(Model.classes_, proba):
        print(f"Classe {cls}: {p:.3f}")

    #Logica para aplicar a classe Neutra com base na probabilidade de outras classes
    if top1 < threshold or (top1 - top2) < delta:
        classe = "Neutro"
    else:
        if Forecast == 0:
            classe = "Negativo"
        elif Forecast == 1:
            classe = "Positivo"
        else:
            classe = "Neutro"
    print('Classe: ', classe)
    #Retornando a classe
    return classe, top1