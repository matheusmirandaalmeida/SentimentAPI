# start_fastapi.ps1
Write-Host "🚀 INICIANDO FASTAPI..." -ForegroundColor Green

# Verificar se está no diretório correto
if (-not (Test-Path "app.py")) {
    Write-Host "❌ Arquivo app.py não encontrado!" -ForegroundColor Red
    Write-Host "💡 Execute este script da pasta ds_service_V2" -ForegroundColor Yellow
    exit 1
}

# Verificar se o modelo existe
if (-not (Test-Path "Tuning_Model.pkl")) {
    Write-Host "⚠️  Arquivo Tuning_Model.pkl não encontrado!" -ForegroundColor Yellow

    # Verificar se tem o arquivo com nome errado
    if (Test-Path "Tuning_Model.pk1") {
        Write-Host "📁 Encontrado Tuning_Model.pk1 - Renomeando..." -ForegroundColor Cyan
        Rename-Item "Tuning_Model.pk1" "Tuning_Model.pkl"
        Write-Host "✅ Arquivo renomeado!" -ForegroundColor Green
    } else {
        Write-Host "❌ Nenhum arquivo de modelo encontrado!" -ForegroundColor Red
        Write-Host "💡 Verifique se o arquivo Tuning_Model.pkl existe" -ForegroundColor Yellow
    }
}

# Verificar Python
try {
    $pythonVersion = python --version
    Write-Host "✅ Python encontrado: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Python não encontrado!" -ForegroundColor Red
    Write-Host "💡 Instale Python 3.8+ e adicione ao PATH" -ForegroundColor Yellow
    exit 1
}

# Verificar dependências
if (Test-Path "requirements.txt") {
    Write-Host "📦 Verificando dependências..." -ForegroundColor Cyan
    pip install -r requirements.txt
} else {
    Write-Host "⚠️  Arquivo requirements.txt não encontrado" -ForegroundColor Yellow
}

# Iniciar FastAPI
Write-Host "▶️  Iniciando FastAPI na porta 8000..." -ForegroundColor Cyan
Write-Host "🔗 URL: http://localhost:8000" -ForegroundColor Cyan
Write-Host "📚 Docs: http://localhost:8000/docs" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pressione Ctrl+C para parar" -ForegroundColor Yellow

python -m uvicorn app:app --reload --host 0.0.0.0 --port 8000