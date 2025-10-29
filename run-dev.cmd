@echo off
REM run-dev.cmd - Starts backend and frontend in separate windows (Windows cmd)

:: Change directory to the script location (repo root)
cd /d "%~dp0"

:: Start backend in a new cmd window
start "Backend - Urban Bites" cmd /k "cd backend && mvnw.cmd spring-boot:run"

:: Wait for backend to respond on http://localhost:8080/api/health (max 180 seconds)
echo Waiting for backend to become available on http://localhost:8080/api/health ...
set "MAX_WAIT=180"
set /A "ELAPSED=0"
set "HEALTH_URL=http://localhost:8080/api/health"

:waitloop
:: Use PowerShell's Invoke-WebRequest to test the backend quickly (5s timeout)
powershell -Command "try { Invoke-WebRequest -UseBasicParsing -Uri '%HEALTH_URL%' -TimeoutSec 5 > $null; exit 0 } catch { exit 1 }"
if %ERRORLEVEL%==0 goto backend_up
if %ELAPSED% GEQ %MAX_WAIT% goto timeout

:: Wait 3 seconds and try again
timeout /t 3 >nul
set /A ELAPSED+=3
goto waitloop

:backend_up
echo Backend is up after %ELAPSED% seconds.

:: Start frontend in a new cmd window
start "Frontend - Urban Bites" cmd /k "cd frontend && npm install && npm start"

goto end

:timeout
echo WARNING: Backend did not respond after %MAX_WAIT% seconds. Starting frontend anyway.
start "Frontend - Urban Bites" cmd /k "cd frontend && npm install && npm start"

:end

echo Started Backend and Frontend in separate windows. Close those windows to stop the services.
pause
