#!/usr/bin/env pwsh

Write-Host "Testing login endpoint..."

$loginData = @{
    email = "admin@example.com"
    password = "admin123"
    encrypted = $false
} | ConvertTo-Json

Write-Host "Request data: $loginData"

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginData

    Write-Host "SUCCESS! Status: $($response.StatusCode)"
    Write-Host "Response:"
    $response.Content
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "FAILED! Status Code: $statusCode"

    if ($statusCode -eq 400) {
        Write-Host "This is a 400 Bad Request - likely validation error"
    }

    try {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = [System.IO.StreamReader]::new($stream)
        $body = $reader.ReadToEnd()
        if ($body) {
            Write-Host "Response body: '$body'"
        } else {
            Write-Host "Response body is empty"
        }
        $reader.Close()
    } catch {
        Write-Host "Could not read response stream: $_"
    }
}

