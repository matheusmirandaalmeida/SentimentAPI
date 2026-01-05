from Data_Tokening import X, Y, embedding_text

#Realizando a divisão dos dados com 80% usado para treino
from sklearn.model_selection import train_test_split
X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size= 0.20, stratify=Y)

#Aplicando o embedding nos dados apenas depois de separar em treino e teste, para que os dados de treino estejam isolados dos dados de testess
import numpy as np #Importando o numpy para converter a lista em um array
X_train = np.array(embedding_text(list(X_train)))

print("X treino\n",X_train.shape[0])
print("\nX testes\n",X_test.shape[0])


#Importanto o modelo SVC que realiza o aprendizado baseado em cortes
from sklearn.svm import SVC

#Inicializando a primeira versão do modelo
SVC_model = SVC(kernel='rbf', C=1.0, verbose=True, probability=True)#Verbose = true, pois são duas classes

#Treinamento do modelo
SVC_model.fit(X_train, Y_train)
print("Treinamento finalizado")


#Fazendo o embedding dos dados de teste
#Pegando as previsões do modelo com os dados de teste
X_test = np.array(embedding_text(list(X_test)))
Y_prev = SVC_model.predict(X_test)


#Validação cruzada do modelo
from sklearn.model_selection import cross_validate, StratifiedKFold

#Dividindo os dados para validar o modelo usando as métricas corretas
skf = StratifiedKFold(n_splits=10, shuffle=True)
#Pegando o modelo que vai ser análisado
model = SVC(kernel='rbf', C=1.0, class_weight='balanced')
#Realizando a validação cruzada com o modelo, os dados X e Y, a divisão dos dados e as métricas
X_emb = np.array(embedding_text(list(X)))#Pegando embedding
cv_resultados = cross_validate(model, X_emb, Y, cv=skf, scoring=['accuracy', 'precision', 'recall', 'f1'], n_jobs=-1)
#Printando resultados
for metric in cv_resultados:
    if metric.startswith('test_'):
        print(
            metric,
            f"média={np.mean(cv_resultados[metric]):.3f}",
            f"std={np.std(cv_resultados[metric]):.3f}"
        )


#Avaliando com a matriz de confusão
from sklearn.metrics import confusion_matrix
from sklearn.metrics import ConfusionMatrixDisplay
import matplotlib.pyplot as plt
#Criando a matriz
matriz_confusao = confusion_matrix(Y_test, Y_prev)
#Visualizando a matriz de confusão
visualizacao = ConfusionMatrixDisplay(matriz_confusao, display_labels=['Positivo', 'Negativo'])
visualizacao.plot()
plt.show()

#Observando como o modelo se saiu
from sklearn.metrics import classification_report
print(classification_report(Y_test, Y_prev))


#Fazendo o modelo novamente, dessa vez com os hiperparâmetros
SVC_model = SVC(probability=True)
#Grid
param_grid = [
    {
        'kernel': ['linear'],
        'C': [0.01, 0.1, 1, 10]
    },
    {
        'kernel': ['rbf'],
        'C': [0.01, 0.1, 1, 10],
        'gamma': ['scale', 0.01, 0.1]
    }
]
#Importando o GridSearchCV e criando o modelo
from sklearn.model_selection import GridSearchCV
Best_SVC = GridSearchCV(
    estimator=SVC_model,#Modelo
    param_grid=param_grid,#Grid de parâmetros
    scoring='f1',#métrica à ser considerada
    cv=skf,#Validação cruzada
    verbose=True,
    n_jobs=-1
)

#Treinamento do modelo
Best_SVC.fit(X_train, Y_train)
#Melhores parâmetros
print("\n\n Melhores parâmetros", Best_SVC.best_params_)

#Observando a nova matriz de confusão
Y_prev = Best_SVC.predict(X_test)
#Criando a matriz
matriz_confusao = confusion_matrix(Y_test, Y_prev)
#Visualizando a matriz de confusão
visualizacao = ConfusionMatrixDisplay(matriz_confusao, display_labels=['Positivo', 'Negativo'])
visualizacao.plot()
plt.show()




"""
precision    recall  f1-score   support

           0       0.90      0.93      0.91       694
           1       0.93      0.89      0.91       694

    accuracy                           0.91      1388
   macro avg       0.91      0.91      0.91      1388
weighted avg       0.91      0.91      0.91      1388
"""
#Foi observado que f1 score foi a melhor métrica a levar em conta, visto que conseguiu realizar uma classificação melhor de falsos negativos sem diminuir a precisão
"""
test_accuracy média=0.914 std=0.009
test_precision média=0.950 std=0.011
test_recall média=0.875 std=0.013
test_f1 média=0.911 std=0.009
"""
#Melhores parâmetros {'C': 1, 'kernel': 'linear'}