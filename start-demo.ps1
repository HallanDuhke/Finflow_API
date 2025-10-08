param(
    [switch]$Rebuild
)
Write-Host "== Finflow API | MODO DEMO (H2) ==" -ForegroundColor Cyan
if ($Rebuild) {
  Write-Host "Limpando e buildando jar..." -ForegroundColor Yellow
  ./mvnw clean package -DskipTests | Out-Host
}
if (-Not (Test-Path target)) {
  Write-Host "Build não encontrado, executando build..." -ForegroundColor Yellow
  ./mvnw -q clean package -DskipTests | Out-Host
}
$jar = Get-ChildItem -Path target -Filter "Finflow_API-*.jar" | Select-Object -First 1
if (-Not $jar) { Write-Error "Jar não encontrado em target"; exit 1 }
Write-Host "Iniciando aplicação com profile demo (H2 em memória)..." -ForegroundColor Green
java -jar $jar.FullName --spring.profiles.active=demo
