#!/usr/bin/env pwsh

# Get current timestamp
$timestamp = [System.DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$plaintext = "admin123|$timestamp"

# Encode to base64
$bytes = [System.Text.Encoding]::UTF8.GetBytes($plaintext)
$encoded = [System.Convert]::ToBase64String($bytes)

Write-Host "Testing login..."
Write-Host "Timestamp: $timestamp"
Write-Host "Encoded password: $encoded"
Write-Host ""

# Create JSON payload
$loginData = @{
    email = "admin@example.com"
    password = $encoded
    encrypted = $true
} | ConvertTo-Json

Write-Host "Request Body:"
Write-Host $loginData
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginData

    Write-Host "SUCCESS! Status Code: $($response.StatusCode)"
    Write-Host ""
    Write-Host "Response:"
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "ERROR! Status Code: $statusCode"
    Write-Host ""

    try {
        $responseBody = $_.Exception.Response.GetResponseStream()
        $reader = [System.IO.StreamReader]::new($responseBody)
        $responseText = $reader.ReadToEnd()
        Write-Host "Response Body:"
        Write-Host $responseText
    } catch {
        Write-Host "Could not read response body"
    }
}

