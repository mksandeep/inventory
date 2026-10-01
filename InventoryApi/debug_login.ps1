#!/usr/bin/env pwsh

# First, let's see if we can get any response from the API
Write-Host "Testing API connectivity..."

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/items" -Method GET -ErrorAction Stop
    Write-Host "API is working. Status: $($response.StatusCode)"
} catch {
    Write-Host "API test failed: $($_.Exception.Message)"
}

# Now test login with simple credentials
Write-Host "`nTesting login with plain password (no encryption)..."
$loginData = '{
    "email": "admin@example.com",
    "password": "admin123",
    "encrypted": false
}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginData `
        -ErrorAction Stop

    Write-Host "Success! Status: $($response.StatusCode)"
    Write-Host $response.Content
} catch {
    Write-Host "Failed with status: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "Exception: $($_.Exception.Message)"

    try {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = [System.IO.StreamReader]::new($stream)
        $body = $reader.ReadToEnd()
        Write-Host "Response body: $body"
    } catch {
        Write-Host "Could not read response"
    }
}

