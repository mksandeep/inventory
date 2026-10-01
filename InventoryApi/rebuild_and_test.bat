@echo off
REM Kill all Java processes
taskkill /F /IM java.exe 2>nul

REM Wait for cleanup
timeout /t 3 /nobreak

REM Build the project
cd /d C:\Users\mksan\git\inventory\InventoryApi
call gradlew.bat build -q

REM Start the application in background
start "InventoryAPI" java -jar build\libs\InventoryApi-1.0-SNAPSHOT.jar

REM Wait for startup
timeout /t 20 /nobreak

REM Test the login
echo.
echo Testing login...
echo.

powershell -Command "$body = '{\"email\":\"admin@example.com\",\"password\":\"admin123\",\"encrypted\":false}'; try { $r = Invoke-WebRequest -Uri http://localhost:8080/api/auth/login -Method POST -ContentType application/json -Body $body -ErrorAction Stop; Write-Host 'SUCCESS! Status:' $r.StatusCode; $r.Content | ConvertFrom-Json | ConvertTo-Json } catch { Write-Host 'FAILED! Status:' $_.Exception.Response.StatusCode.value__; $stream = $_.Exception.Response.GetResponseStream(); $reader = [System.IO.StreamReader]::new($stream); $body = $reader.ReadToEnd(); Write-Host 'Response: '$body }"

pause

