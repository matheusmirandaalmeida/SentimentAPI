# ==============================
# DS SERVICE - PYTHON
# ==============================

# Imagem base Python
FROM python:3.10-slim

# Evita geração de arquivos .pyc
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Diretório de trabalho
WORKDIR /app

# Copia apenas o requirements primeiro (cache de dependências)
COPY requirements.txt .

# Atualiza pip e instala dependências
RUN pip install --upgrade pip \
    && pip install --no-cache-dir -r requirements.txt

# Copia todo o código do serviço de Data Science
COPY . .

# Expõe a porta usada pelo serviço (8000)
EXPOSE 8000

# Comando para iniciar a API
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
