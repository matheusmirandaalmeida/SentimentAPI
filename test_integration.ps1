# test_final.ps1
Write-Host "🎯 TESTE FINAL DE INTEGRAÇÃO" -ForegroundColor Green
Write-Host "============================="

# 1. Verificar FastAPI
Write-Host "`n[1/3] Testando FastAPI direto..." -ForegroundColor Cyan
try {
    $fastapiResult = Invoke-RestMethod -Uri "http://localhost:8000/predict" `
        -Method Post `
        -Body (@{text="salario alto"} | ConvertTo-Json) `
        -ContentType "application/json"

    Write-Host "✅ FastAPI OK:" -ForegroundColor Green
    Write-Host "   Label: $($fastapiResult.label)" -ForegroundColor White
    Write-Host "   Score: $($fastapiResult.score)" -ForegroundColor White
    Write-Host "   Label ID: $($fastapiResult.label_id)" -ForegroundColor White
    Write-Host "   Translated: $($fastapiResult.translated)" -ForegroundColor White
} catch {
    Write-Host "❌ FastAPI Error: $_" -ForegroundColor Red
    exit 1
}

# 2. Verificar Spring Boot
Write-Host "`n[2/3] Testando Spring Boot..." -ForegroundColor Cyan
try {
    $springResult = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/sentiment" `
        -Method Post `
        -Body (@{text="salario alto"} | ConvertTo-Json) `
        -ContentType "application/json" `
        -ErrorAction Stop

    Write-Host "✅ Spring Boot OK:" -ForegroundColor Green
    Write-Host "   Previsao: $($springResult.previsao)" -ForegroundColor White
    Write-Host "   Probabilidade: $($springResult.probabilidade)" -ForegroundColor White

} catch {
    Write-Host "❌ Spring Boot Error: $_" -ForegroundColor Red
    Write-Host "`n🔧 Solução:" -ForegroundColor Yellow
    Write-Host "1. Certifique-se de que o Spring Boot está rodando no IntelliJ" -ForegroundColor White
    Write-Host "2. Verifique os logs do Spring Boot" -ForegroundColor White
    Write-Host "3. Confirme application.properties:" -ForegroundColor White
    Write-Host "   ds.service.base-url=http://localhost:8000" -ForegroundColor White
    exit 1
}

# 3. Teste do frontend
Write-Host "`n[3/3] Testando frontend (opcional)..." -ForegroundColor Cyan
try {
    $frontend = Invoke-WebRequest -Uri "http://localhost:5500" -ErrorAction SilentlyContinue
    if ($frontend.StatusCode -eq 200) {
        Write-Host "✅ Frontend está acessível" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  Frontend não detectado (pode ser normal)" -ForegroundColor Yellow
}

Write-Host "`n🎉 SISTEMA FUNCIONANDO PERFEITAMENTE!" -ForegroundColor Green
Write-Host "========================================"
Write-Host "FastAPI:  http://localhost:8000/docs" -ForegroundColor Cyan
Write-Host "Spring:   http://localhost:8080/api/v1/sentiment" -ForegroundColor Cyan
Write-Host "H2 DB:    http://localhost:8080/h2-console" -ForegroundColor Cyan