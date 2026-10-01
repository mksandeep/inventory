#!/usr/bin/env pwsh

# Get current timestamp in milliseconds
$timestamp = [System.DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$plaintext = "admin123|$timestamp"

# Convert to base64
$bytes = [System.Text.Encoding]::UTF8.GetBytes($plaintext)
$encoded = [System.Convert]::ToBase64String($bytes)

Write-Host "Timestamp: $timestamp"
Write-Host "Plain text: $plaintext"
Write-Host "Base64 encoded: $encoded"

# Test the login endpoint
$loginData = @{
    email = "admin@example.com"
    password = $encoded
    encrypted = $true
} | ConvertTo-Json

Write-Host "`nTesting login with endpoint http://localhost:8080/api/auth/login"
Write-Host "Payload: $loginData"

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginData `
    -ErrorAction SilentlyContinue

if ($response.StatusCode -eq 200) {
    Write-Host "Login successful!"
    Write-Host $response.Content | ConvertFrom-Json | ConvertTo-Json
} else {
    Write-Host "Login failed with status code: $($response.StatusCode)"
    Write-Host $response.Content
}

