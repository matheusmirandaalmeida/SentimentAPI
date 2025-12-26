import pandas as pd

#Importando os dados
dados = pd.read_csv("C:/Users/pedro/Desktop/Tech Journey/Hackatoon_OracleAlura/DataScience/Glassdoor Reviews.csv")
#Observando os dados
print(dados.info())
print(dados.head())

#Filtrando os comentarios Pros e Cons
dados = dados[['pros','cons']]

#Removendo dados duplicados
print(dados.duplicated().sum())
dados.drop_duplicates(inplace=True)

#Separando os dados em dataframes diferentes
dados_negativos = pd.DataFrame(dados['cons']).rename(columns={'cons':'Comentary'})
dados_positivos =  pd.DataFrame(dados['pros']).rename(columns={'pros':'Comentary'})
#Atribuindo o sentimento com base no data frame
dados_negativos["Feeling"] = "Negative"
#print(dados_negativos.head())
dados_positivos["Feeling"] = "Positive"
#print(dados_positivos.head())
#Juntando os dataframes em um só apenas
data_feelings = pd.concat([dados_positivos, dados_negativos], ignore_index=True)
print(data_feelings['Feeling'].value_counts())

#Embaralhando o dataset para utilizar no modelo
data_feelings=data_feelings.sample(frac=1, random_state=42).reset_index(drop=True)

#Convertendo as variaveis explicativas binarias, no caso feelings
data_feelings['Feeling']=data_feelings['Feeling'].map({'Positive':1,'Negative':0})

#Salvando o novo DataFrame Tratado
data_feelings.to_csv('C:/Users/pedro/Desktop/Tech Journey/Hackatoon_OracleAlura/DataScience/DataFeelings.csv',index=False)

#Lendo e verificando
dados = pd.read_csv('C:/Users/pedro/Desktop/Tech Journey/Hackatoon_OracleAlura/DataScience/DataFeelings.csv')
print(dados.head())

