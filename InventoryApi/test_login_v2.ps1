#!/usr/bin/env pwsh

# Get current timestamp
$timestamp = [System.DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$plaintext = "admin123|$timestamp"

# Encode to base64
$bytes = [System.Text.Encoding]::UTF8.GetBytes($plaintext)
$encoded = [System.Convert]::ToBase64String($bytes)

Write-Host "Timestamp: $timestamp"
Write-Host "Plaintext: $plaintext"
Write-Host "Encoded: $encoded"
Write-Host ""

# Create JSON payload
$loginData = @{
    email = "admin@example.com"
    password = $encoded
    encrypted = $true
} | ConvertTo-Json

Write-Host "Payload:"
Write-Host $loginData
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginData

    Write-Host "SUCCESS! Status Code: $($response.StatusCode)"
    Write-Host "Response:"
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
} catch {
    Write-Host "ERROR! Status Code: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "Error Message: $($_.Exception.Message)"
    Write-Host "Response:"
    Write-Host $_.Exception.Response.StatusDescription
}

