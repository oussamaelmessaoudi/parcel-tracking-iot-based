Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  WSL2 Port Forwarding Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get WSL2 IP
$wslIP = (wsl hostname -I).Trim().Split()[0]
Write-Host "📍 WSL2 IP: $wslIP" -ForegroundColor Yellow
Write-Host ""

# Remove old port forwarding
Write-Host "🗑️  Removing old port forwarding..." -ForegroundColor Yellow
netsh interface portproxy delete v4tov4 listenport=8080 listenaddress=0.0.0.0 2>$null
netsh interface portproxy delete v4tov4 listenport=1883 listenaddress=0.0.0.0 2>$null

# Add new port forwarding
Write-Host "✨ Adding new port forwarding..." -ForegroundColor Yellow
netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=8080 connectaddress=$wslIP
netsh interface portproxy add v4tov4 listenport=1883 listenaddress=0.0.0.0 connectport=1883 connectaddress=$wslIP

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  ✅ Port Forwarding Active" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Current Configuration:" -ForegroundColor White
netsh interface portproxy show all
Write-Host ""
Write-Host "🌐 Access URLs:" -ForegroundColor White
Write-Host "   Spring API: http://localhost:8080/api/health" -ForegroundColor Cyan
Write-Host "   Mosquitto: mqtt://localhost:1883" -ForegroundColor Cyan
Write-Host ""

# Optional: Open browser
# Start-Process "http://localhost:8080/api/health"

Read-Host "Press Enter to exit"