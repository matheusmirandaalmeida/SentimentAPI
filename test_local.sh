#!/bin/bash

echo "🚀 TESTE LOCAL - SENTIMENT API"
echo "==============================="

echo "1. Iniciando FastAPI..."
cd ds_service_V2
python -m uvicorn app:app --reload --host 0.0.0.0 --port 8000 &
FASTAPI_PID=$!
cd ..

echo "2. Aguardando FastAPI iniciar..."
sleep 3

echo "3. Testando FastAPI..."
curl -s http://localhost:8000/health | python -m json.tool

echo "4. Testando endpoint predict..."
curl -X POST http://localhost:8000/predict \
  -H "Content-Type: application/json" \
  -d '{"text":"good salary"}' \
  -w "\nHTTP Status: %{http_code}\n"

echo "5. Agora inicie o Spring Boot no IntelliJ e teste:"
echo "   POST http://localhost:8080/api/v1/sentiment"
echo '   Body: {"text":"salary high"}'

echo ""
echo "📝 Para parar o FastAPI: kill $FASTAPI_PID"