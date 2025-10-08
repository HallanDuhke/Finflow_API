param(
  [switch]$Rebuild
)
Write-Host "== Finflow API | DOCKER MODE ==" -ForegroundColor Cyan
if ($Rebuild) {
  Write-Host "Forçando rebuild de imagens..." -ForegroundColor Yellow
  docker compose build --no-cache | Out-Host
}
Write-Host "Subindo serviços (API + Postgres)..." -ForegroundColor Green
docker compose up -d
Write-Host "Logs iniciais (CTRL+C para sair)..." -ForegroundColor Cyan
docker compose logs -f app