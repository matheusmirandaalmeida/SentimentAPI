#Importanto a biblioteca para traduzir o comentario
from deep_translator import GoogleTranslator

#Função para traduzir qualquer idioma para o inglês
def translate_commentary(input_text):
    try:
        #O source = 'auto' detecta o idioma automaticamente e define o idioma à traduzir
        translator = GoogleTranslator(source='auto', target='en')
        #Traduzindo o texto
        text_translated = translator.translate(input_text)
        print(text_translated)
    except Exception as e:
        return f'Erro de tradução'
    #Retornando o texto traduzido
    return text_translated
