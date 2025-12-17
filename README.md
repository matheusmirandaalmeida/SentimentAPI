📊 SentimentAPI

API de análise de sentimentos desenvolvida como base para o projeto final do Hackathon Oracle + Alura.
O sistema recebe textos (comentários, avaliações ou opiniões), classifica o sentimento e retorna a previsão com uma probabilidade associada.

🎯 Objetivo do Projeto

Ajudar empresas a entender rapidamente o sentimento de clientes ou usuários, permitindo:
Identificar reclamações e elogios
Priorizar atendimentos negativos
Medir satisfação ao longo do tempo
Este projeto serve como um MVP funcional, pronto para evolução.

🧠 Visão Geral da Solução

O sistema é dividido em dois serviços independentes, que se comunicam via HTTP:

🔹 1. API Principal — Java (Spring Boot)

Responsável por:
Expor o endpoint REST
Validar entradas
Integrar com o serviço de Data Science
Persistir logs das análises
Retornar a resposta ao cliente

🔹 2. Serviço de Data Science — Python (FastAPI)

Responsável por:
Receber o texto
Classificar o sentimento
Retornar previsão e probabilidade
(Modelo inicial simples, preparado para futura evolução com ML)

🔄 Fluxo de Funcionamento
Cliente
  ↓
SentimentAPI (Java / Spring Boot)
  ↓
DS-Service (Python / FastAPI)
  ↓
Banco H2 (logs)
  ↓
Resposta JSON

🗄️ Persistência de Dados

Cada análise é salva no banco H2, armazenando:
Texto analisado
Previsão do sentimento
Probabilidade
Data e hora da análise
Origem da requisição
Esses dados poderão ser usados futuramente para:
Dashboards
Métricas
Relatórios
Re-treinamento de modelos

🛠️ Tecnologias Utilizadas
Backend

Java 21+

Spring Boot

Spring Web

Spring Data JPA

H2 Database

RestTemplate

Lombok

Data Science Service

Python 3.11

FastAPI

Uvicorn

Pydantic

▶️ Como Executar
1️⃣ Serviço de Data Science
cd ds-service
pip install -r requirements.txt
uvicorn app:app --reload --port 8000

2️⃣ API Java
./mvnw spring-boot:run


A API estará disponível em:

http://localhost:8080/api/v1/sentiment

🚀 Status do Projeto

✔️ MVP funcional
✔️ Integração Java + Python
✔️ Persistência de dados
