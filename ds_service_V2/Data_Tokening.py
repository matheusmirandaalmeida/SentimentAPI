import pandas as pd
import numpy as np
import os
#Importando as classes da biblioteca huggingFace que permite tokenizar o modelo e gerar embeddings e o framework pytorch, para excecutar o modelo da melhor forma
import torch
from transformers import RobertaTokenizer, RobertaModel


#Nome do modelo pre treinado que iremos usar
MODEL_NAME = 'roberta-base' 

#Pegando o tokenizador e o modelo do BERT, baixando o modelo da hugging face
tokenizer = RobertaTokenizer.from_pretrained(MODEL_NAME)#Carregando o tokenizador
bert_model = RobertaModel.from_pretrained(MODEL_NAME)#Carregando os pesos do modelo

#colocando o modelo em modo de avaliação para evitar comportamentos aleatorios, já que só queremos tokenizar o texto
bert_model.eval()

#Função para realizar o embedding dos textos
def embedding_text(text):
    #Lista para armazenar os lotes
    all_embeddings = []

    #Processando o texto recebido em lotes
    for i in range(0, len(text), 32):
        #Separando o texto em lotes de 32 bits para evitar estourar a memória
        batch_texts = text[i:i+32]
        

        #Tokenizando o lote em números que o modelo entende, utilizando a tokenização da roberta
        encoded_input = tokenizer(
            batch_texts,#Texto à ser tokenizado
            padding = True,#Todos os tokens vão ter o mesmo tamanho
            truncation = True,#Se o texto for maior que o límite, ele é cortado
            max_length=128,
            return_tensors = 'pt'#Tensores serão referênciados ao pytorch
        )

        #Desativando o calculo de gradientes, visto que não estamos treinando nada, apenas calculando o gradiente
        with torch.no_grad():
            #Executando o modelo
            outputs = bert_model(**encoded_input)
            #Output[0] é o tamanho do lote e [:,0,:] é o token que representa o significado global do texto e converte o tensor do pytorch em um array em numpy
            batch_embeddings = outputs.last_hidden_state[:,0,:]

        #Armazendando o embedding em no lote atual
        all_embeddings.append(batch_embeddings.cpu().numpy())

    #Juntando todos os lotes verticalmente e retornando
    return np.vstack(all_embeddings)


#Função para criar e rodar embeddings
def get_or_create_embeddings(texts, emb_path):
    if os.path.exists(emb_path):
        print(f"Carregando embeddings X: {emb_path}")
        return np.load(emb_path)

    print(f"Gerando embeddings X: {emb_path}")
    X_emb = np.array(embedding_text(list(texts)))
    np.save(emb_path, X_emb)

    return X_emb



#Salvando os embendding
def save_embeddings(X, Y, x_path, y_path):
    np.save(x_path, X)
    np.save(y_path, Y)
#Carregando os embedding
def load_embeddings(x_path, y_path):
    X = np.load(x_path)
    Y = np.load(y_path)
    return X, Y