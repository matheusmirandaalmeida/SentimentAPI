# Sentiment Analysis Platform
## Hackathon Oracle & Alura

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Python](https://img.shields.io/badge/Python-3.11-blue)
![FastAPI](https://img.shields.io/badge/FastAPI-0.111.0-teal)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Uma plataforma completa de análise automática de sentimentos aplicada a comentários e avaliações de clientes, desenvolvida com arquitetura de microserviços, Machine Learning e dashboard em tempo real.

Projeto desenvolvido para o Hackathon Oracle & Alura, atendendo 100% dos requisitos técnicos e indo além com funcionalidades extras.

---

## Sumário
- Visão do Negócio
- Funcionalidades
- Arquitetura
- Tecnologias
- Execução do Projeto
- Uso da API
- Machine Learning
- Autenticação e Segurança
- Dashboard Administrativo
- Testes
- Deploy
- Estrutura do Projeto
- Status do Hackathon
- Autores
- Licença

---

## Visão do Negócio

### Problema
Empresas recebem um grande volume de avaliações diariamente e não conseguem:
- Ler todos os comentários manualmente
- Identificar rapidamente reclamações críticas
- Priorizar ações com base na percepção do cliente

### Solução
Uma plataforma que:
- Classifica automaticamente o sentimento do comentário
- Centraliza avaliações em um dashboard
- Prioriza feedbacks negativos
- Oferece API REST para integração com sistemas externos

### Áreas Beneficiadas
- Atendimento ao Cliente
- Marketing
- Operações
- Produto

---

## Funcionalidades

### Análise de Sentimentos
- Classificação trinária: Positivo, Neutro e Negativo
- Modelo RoBERTa (Hugging Face) fine-tuned
- Tradução automática PT → EN
- Retorno de probabilidade (0–100%)
- Latência média inferior a 2 segundos

---

### Dashboard em Tempo Real
- Percentual de sentimentos
- Total de avaliações processadas
- Gráficos interativos (Chart.js)
- Lista de comentários recentes
- Filtros por tipo de sentimento

---

### API RESTful
- Endpoint único para análise de sentimento
- Validação robusta de entradas
- Respostas padronizadas em JSON
- Documentação automática (Swagger / OpenAPI)

---

### Segurança
- Autenticação por email e senha
- Controle de acesso por roles (ADMIN / USER)
- Senhas criptografadas com BCrypt
- CORS configurado para desenvolvimento
- Headers de segurança HTTP

---

## Arquitetura do Sistema

```
┌──────────────────────────────────────────────┐
│ FRONTEND (HTML / CSS / JS)                   │
│ http://localhost:8080                        │
└───────────────────┬──────────────────────────┘
                    │
┌───────────────────▼──────────────────────────┐
│ SPRING BOOT API (Java)                       │
│ Controllers | Services | JPA | Security      │
│ PostgreSQL                                   │
└───────────────────┬──────────────────────────┘
                    │
┌───────────────────▼──────────────────────────┐
│ MICROSERVIÇO ML (Python + FastAPI)           │
│ RoBERTa | Tradução | Classificação           │
└──────────────────────────────────────────────┘
```

---

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Lombok

### Machine Learning
- Python 3.11
- FastAPI
- PyTorch
- Hugging Face Transformers
- scikit-learn
- deep-translator

### Frontend
- HTML5 semântico
- CSS3 moderno
- JavaScript (ES6+)
- Chart.js
- Font Awesome

### Infraestrutura
- Docker
- Docker Compose
- Maven

---

## Execução do Projeto

### Pré-requisitos
- Java 17+
- Python 3.11+
- Maven 3.9+
- Docker (opcional)

---

### Execução Local (Desenvolvimento)

#### Microserviço ML
```bash
cd ds_service_V2
pip install -r requirements.txt
python -m uvicorn app:app --reload --host 0.0.0.0 --port 8000
```

#### Spring Boot API
```bash
mvn clean spring-boot:run
```

#### Acessos
- Frontend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- ML Docs: http://localhost:8000/docs

---

### Execução com Docker
```bash
docker-compose up --build
```

Parar serviços:
```bash
docker-compose down
```

---

## Uso da API

### Endpoint Principal
```http
POST /api/v1/sentiment
```

### Exemplo de Requisição
```bash
curl -X POST http://localhost:8080/api/v1/sentiment -H "Content-Type: application/json" -d '{"text": "Atendimento excelente, funcionários muito atenciosos!"}'
```

### Resposta
```json
{
  "previsao": "POSITIVO",
  "probabilidade": 0.92,
  "translated": "Excellent service, very attentive staff!"
}
```

---

## Machine Learning

### Pipeline
Texto em PT → Tradução → Tokenização → Embeddings → Classificação → Regra de Neutro

### Métricas
- Acurácia aproximada: 85%
- Latência inferior a 2 segundos
- Classes:
  - Positivo (1)
  - Negativo (0)
  - Neutro (2)

---

## Autenticação, Roles e Persistência (PostgreSQL)

### Papéis de Usuário

| Role  | Descrição |
|------|----------|
| USER | Usuário comum que pode enviar mensagens para análise de sentimento |
| ADMIN | Usuário administrativo com acesso ao dashboard e métricas |

---

### Fluxo de Login (MVP)

Endpoint:
```
POST /api/v1/auth/login
```

Request:
```json
{
  "username": "admin",
  "password": "123"
}
```

Response:
```json
{
  "username": "admin",
  "role": "ADMIN"
}
```

### Regra de Negócio
- Username igual a admin recebe role ADMIN
- Demais usuários recebem role USER

Autenticação mockada nesta fase do MVP, com estrutura preparada para persistência real em PostgreSQL.

---

### Proteção de Rotas
Header obrigatório:
```
X-ROLE: ADMIN
```

Comportamento esperado:
- ADMIN: acesso liberado
- Outros: HTTP 403 (Forbidden)

---

## Dashboard Administrativo
- Percentual de sentimentos
- Gráfico de pizza
- Comentários recentes
- Monitoramento de saúde dos serviços

---

## Testes
- Testes manuais via Swagger
- Testes com Postman
- Health checks dos serviços
- Script de integração

---

## Status do Hackathon
- Notebook de Data Science com modelo treinado
- API REST funcional
- Classificação trinária
- PostgreSQL configurado
- Arquitetura de microserviços
- Dashboard em tempo real

---

## Autores
Projeto desenvolvido para o Hackathon Oracle & Alura.

- Diego Santos
- Matheus Miranda Almeida
- Pedro Machado
- Victor Albuquerque
- Webster Spacacheri

---

## Licença
Este projeto está sob a licença MIT.

---

## Agradecimentos
- Oracle & Alura
- Mentores e Professores
- Comunidade Open Source (Bibliotecas)
