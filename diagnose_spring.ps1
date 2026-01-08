# diagnose_spring.ps1
Write-Host "🔍 DIAGNÓSTICO DO SPRING BOOT" -ForegroundColor Cyan
Write-Host "================================="

# 1. Verificar porta 8080
Write-Host "`n[1] Verificando porta 8080..." -ForegroundColor Yellow
$portCheck = netstat -an | findstr ":8080"
if ($portCheck) {
    Write-Host "✅ Porta 8080 em uso:" -ForegroundColor Green
    $portCheck
} else {
    Write-Host "❌ Porta 8080 não está em uso (Spring Boot não está rodando)" -ForegroundColor Red
}

# 2. Testar endpoints básicos
Write-Host "`n[2] Testando endpoints..." -ForegroundColor Yellow

$endpoints = @(
    @{Url="http://localhost:8080"; Method="GET"; Name="Raiz"},
    @{Url="http://localhost:8080/actuator/health"; Method="GET"; Name="Health"},
    @{Url="http://localhost:8080/h2-console"; Method="GET"; Name="H2 Console"}
)

foreach ($ep in $endpoints) {
    Write-Host "  Testando $($ep.Name)..." -NoNewline
    try {
        $response = Invoke-WebRequest -Uri $ep.Url -Method $ep.Method -ErrorAction SilentlyContinue
        Write-Host " ✅ ($($response.StatusCode))" -ForegroundColor Green
    } catch {
        Write-Host " ❌ ($($_.Exception.Message))" -ForegroundColor Red
    }
}

# 3. Verificar FastAPI
Write-Host "`n[3] Verificando FastAPI..." -ForegroundColor Yellow
try {
    $fastapi = Invoke-RestMethod -Uri "http://localhost:8000/health" -Method Get
    Write-Host "✅ FastAPI OK: $($fastapi.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ FastAPI Error" -ForegroundColor Red
}

# 4. Tentar compilar
Write-Host "`n[4] Tentando compilar projeto..." -ForegroundColor Yellow
cd C:\Users\teumm\Downloads\SentimentAPI\SentimentAPI
mvn compile 2>&1 | Select-String -Pattern "ERROR|BUILD FAILURE|cannot find symbol" -Context 2

Write-Host "`n🎯 DIAGNÓSTICO CONCLUÍDO" -ForegroundColor Cyan