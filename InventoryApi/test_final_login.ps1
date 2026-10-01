#!/usr/bin/env pwsh

Write-Host "Testing login with plain password..."
$body = '{
  "email": "admin@example.com",
  "password": "admin123",
  "encrypted": false
}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body `
        -ErrorAction Stop

    Write-Host "SUCCESS! Status: $($response.StatusCode)"
    Write-Host ""
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
} catch {
    $status = $_.Exception.Response.StatusCode.value__
    Write-Host "FAILED! Status Code: $status"
    Write-Host "Message: $($_.Exception.Message)"
}

