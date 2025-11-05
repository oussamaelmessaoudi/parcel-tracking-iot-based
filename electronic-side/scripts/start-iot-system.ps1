#Requires -RunAsAdministrator

param(
    [switch]$Stop,
    [switch]$Restart,
    [switch]$Logs
)

$PROJECT_PATH = "C:\Users\US4Moooow\Desktop\Spring projects\parcel-tracking-iot-based\electronic-side"

function Show-Banner {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  IoT Tracking System Manager" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
}

function Free-Ports {
    Write-Host "[*] Checking and freeing used ports (1883, 8080)..." -ForegroundColor Yellow

    $ports = @(1883, 8080)
    foreach ($port in $ports) {
        $connections = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($connections) {
            foreach ($conn in $connections) {
                $procId = $conn.OwningProcess
                $procName = (Get-Process -Id $procId -ErrorAction SilentlyContinue).ProcessName
                Write-Host "    Port $port in use by PID $procId ($procName)" -ForegroundColor Red
            }

            # Try WSL network reset instead of killing wslrelay
            Write-Host "    Resetting WSL networking to release port $port..." -ForegroundColor Yellow
            wsl -d Ubuntu bash -c "sudo ss -tulwn | grep $port && sudo fuser -k $port/tcp 2>/dev/null || true"
            Start-Sleep -Seconds 2

            Write-Host "    Port $port freed." -ForegroundColor Green
        } else {
            Write-Host "    Port $port is free." -ForegroundColor Gray
        }
    }

    Start-Sleep -Seconds 3
    Write-Host ""
}



function Setup-PortForwarding {
    Write-Host "[*] Setting up port forwarding..." -ForegroundColor Yellow
    
    $wslIP = (wsl -d Ubuntu bash -c "hostname -I").Trim().Split()[0]
    Write-Host "    WSL2 IP: $wslIP" -ForegroundColor Gray
    
    netsh interface portproxy delete v4tov4 listenport=8080 listenaddress=0.0.0.0 2>$null
    netsh interface portproxy delete v4tov4 listenport=1883 listenaddress=0.0.0.0 2>$null
    
    netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=8080 connectaddress=$wslIP | Out-Null
    netsh interface portproxy add v4tov4 listenport=1883 listenaddress=0.0.0.0 connectport=1883 connectaddress=$wslIP | Out-Null
    
    Write-Host "    [OK] Port forwarding configured" -ForegroundColor Green
    Write-Host ""
}

function Start-System {
    Show-Banner
    Write-Host "[*] Starting IoT Tracking System..." -ForegroundColor Yellow
    Write-Host ""

    Free-Ports
    Setup-PortForwarding

    Write-Host "[*] Starting Docker containers..." -ForegroundColor Yellow
    $wslPath = $PROJECT_PATH -replace 'C:', '/mnt/c' -replace '\\', '/'
    
    wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose up -d"
    
    Write-Host ""
    Write-Host "[*] Waiting for services to initialize (30 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    Write-Host ""
    Write-Host "[*] Container Status:" -ForegroundColor Cyan
    wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose ps"
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  System Ready!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Access URLs:" -ForegroundColor White
    Write-Host "  Spring API: http://localhost:8080/api/health" -ForegroundColor Cyan
    Write-Host "  Latest Data: http://localhost:8080/api/sensor/latest" -ForegroundColor Cyan
    Write-Host "  Mosquitto: mqtt://localhost:1883" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Useful Commands:" -ForegroundColor White
    Write-Host "  View logs: .\start-iot-system.ps1 -Logs" -ForegroundColor Gray
    Write-Host "  Restart: .\start-iot-system.ps1 -Restart" -ForegroundColor Gray
    Write-Host "  Stop: .\start-iot-system.ps1 -Stop" -ForegroundColor Gray
    Write-Host ""
    
    $openBrowser = Read-Host "Open browser? (y/n)"
    if ($openBrowser -eq 'y') {
        Start-Process "http://localhost:8080/api/health"
    }
}

function Stop-System {
    Show-Banner
    Write-Host "[*] Stopping IoT Tracking System..." -ForegroundColor Yellow
    Write-Host ""
    
    $wslPath = $PROJECT_PATH -replace 'C:', '/mnt/c' -replace '\\', '/'
    wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose down"
    
    Write-Host ""
    Write-Host "[OK] System stopped" -ForegroundColor Green
    Write-Host ""
}

function Restart-System {
    Show-Banner
    Write-Host "[*] Restarting IoT Tracking System..." -ForegroundColor Yellow
    Write-Host ""
    
    $wslPath = $PROJECT_PATH -replace 'C:', '/mnt/c' -replace '\\', '/'
    wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose down"
    
    Free-Ports

    Write-Host "    Rebuilding..." -ForegroundColor Yellow
    wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose up -d --build"
    
    Write-Host ""
    Write-Host "[*] Waiting for services..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    Setup-PortForwarding
    
    Write-Host "[OK] System restarted" -ForegroundColor Green
    Write-Host ""
}

function Show-Logs {
    Show-Banner
    Write-Host "[*] Which logs do you want to view?" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "1. Spring App (mqttrestapp)" -ForegroundColor White
    Write-Host "2. Mosquitto Broker" -ForegroundColor White
    Write-Host "3. Kafka" -ForegroundColor White
    Write-Host "4. All containers" -ForegroundColor White
    Write-Host ""
    
    $choice = Read-Host "Enter choice (1-4)"
    
    $wslPath = $PROJECT_PATH -replace 'C:', '/mnt/c' -replace '\\', '/'
    
    switch ($choice) {
        "1" { wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose logs -f mqttrestapp" }
        "2" { wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose logs -f mosquitto" }
        "3" { wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose logs -f kafka" }
        "4" { wsl -d Ubuntu bash -c "cd '$wslPath' && docker compose logs -f" }
        default { Write-Host "Invalid choice" -ForegroundColor Red }
    }
}

if ($Stop) {
    Stop-System
}
elseif ($Restart) {
    Restart-System
}
elseif ($Logs) {
    Show-Logs
}
else {
    Start-System
}

Write-Host "Press Enter to exit..."
Read-Host
