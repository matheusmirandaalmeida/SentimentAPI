#Importando a função de embedding
from Data_Tokening import embedding_text
#Importando a função para definir a classe neutra
from Neutral_Class import Neutral_Definition
#Importando a função que traduz o texto para inglês
from Translator import translate_commentary
#Importando a biblioteca flask para crar a aplicação web, permitir acessar dados vindos de um Json e mandar dados em formato json
from flask import Flask, request, jsonify
import pickle
import os

#Carregando o modelo aqui
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "..", "DataScience", "Tuning_Model.pkl")

with open(MODEL_PATH, "rb") as f:
    BestModel = pickle.load(f)


#Criando a aplicação Flask
app = Flask(__name__)#_name_ ajuda a localizar os arquivos e as configurações

#Criando um endpoint para responder a métodos POSTS
@app.route('/predict_sentiment', methods=['POST'])
#Função para receber os comentarios
def predict_sentiment():
    #Extraindo a requisição em formato Json
    data = request.get_json()
    #Acessando o campo do comentário
    comment = data['comment']
    print(comment)
    
    #Traduzindo o comentário
    trad_comment = translate_commentary(comment)
    
    #Tokenizando o comentario e aplicando o modelo
    embedding_comment = embedding_text([trad_comment])
    
    #Previsão do modelo
    prediction = BestModel.predict(embedding_comment)

    #Verificando o sentimento e pegando a probabilidade
    sentiment, prediction_proba = Neutral_Definition(prediction, BestModel, embedding_comment)
    
    print('Sentimento: ', sentiment, '\nPrediction Proba: ', prediction_proba)
    
    #Adcionando as informações à um json
    return jsonify({
        "Input comment": trad_comment,
        "Sentiment": sentiment,
        "Probability": str(prediction_proba)
    })

#Iniciando o servido FLASK
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)