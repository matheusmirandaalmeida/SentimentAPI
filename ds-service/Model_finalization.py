from Model import Best_SVC, X_test, Y_test,Y_prev
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

#Pegando as métricas finais e apresentando em um gráfico
from sklearn.metrics import classification_report

# Gerando o relatório como dicionário
report = classification_report(Y_test, Y_prev, output_dict=True)
# Convertendo para DataFrame
df_metrics = pd.DataFrame(report).transpose()

# Selecionando apenas métricas relevantes das classes
df_plot = df_metrics.loc[['0', '1'], ['precision', 'recall', 'f1-score']]
#Plotando o gráfico
plt.figure(figsize=(8, 5))
sns.barplot(data=df_plot)
plt.title('Métricas do Modelo Final (SVC + RoBERTa)')
plt.ylabel('Score')
plt.ylim(0, 1)
plt.grid(axis='y')
plt.show()


#Apesentando a curva ROC do modelo
from sklearn.metrics import roc_curve, auc

# Probabilidades da classe positiva
y_proba = Best_SVC.predict_proba(X_test)[:, 1]
# Calculando curva ROC
fpr, tpr, thresholds = roc_curve(Y_test, y_proba)
roc_auc = auc(fpr, tpr)

# Plot
plt.figure(figsize=(7, 6))
plt.plot(fpr, tpr, label=f'ROC curve (AUC = {roc_auc:.3f})')
plt.plot([0, 1], [0, 1], linestyle='--', label='Classificador Aleatório')
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.title('Curva ROC – Modelo Final')
plt.legend(loc='lower right')
plt.grid()
plt.show()


#Salvando o modelo para ser usado depois usando pickle
import pickle

#Abrindo um arquivo no formato de escrita binária
#Utilizando um tratamento de exessão para ter certeza 
path = r'C:\Users\pedro\Desktop\Tech Journey\Hackatoon_OracleAlura\DataScience\Best_SVC.pkl'
try:
    with open(path, 'wb') as file:
        pickle.dump(Best_SVC, file)
    print('Modelo salvo com sucesso')
except Exception as e:
    print('Erro ao salvar o modelo:', e)
