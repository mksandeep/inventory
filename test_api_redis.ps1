#!/usr/bin/env pwsh

# Test script for Redis connection issue fix
# This script tests the /api/products endpoint

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Inventory API - Redis Connection Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if API is running
Write-Host "Checking if API is running on http://localhost:8080..." -ForegroundColor Yellow
$apiUrl = "http://localhost:8080/api/products"

try {
    $response = Invoke-WebRequest -Uri $apiUrl -Method GET -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓ API is running!" -ForegroundColor Green
    Write-Host "  Status Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""

    Write-Host "Response Preview:" -ForegroundColor Yellow
    $data = $response.Content | ConvertFrom-Json
    Write-Host "  Total products: $($data.Count)" -ForegroundColor Green

    if ($data.Count -gt 0) {
        Write-Host "  First product:" -ForegroundColor Green
        $firstProduct = $data[0]
        Write-Host "    - ID: $($firstProduct.productId)" -ForegroundColor Gray
        Write-Host "    - SKU: $($firstProduct.sku)" -ForegroundColor Gray
        Write-Host "    - Category: $($firstProduct.category)" -ForegroundColor Gray
    }

    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "✓ SUCCESS: API is working!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan

} catch {
    Write-Host "✗ API is not running or error occurred" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "To start the API, run:" -ForegroundColor Yellow
    Write-Host "  cd C:\Users\mksan\git\inventory\InventoryApi" -ForegroundColor Yellow
    Write-Host "  java -jar build/libs/InventoryApi-1.0-SNAPSHOT.jar" -ForegroundColor Yellow
    Write-Host ""
}

# Optional: Check Redis connection
Write-Host "Checking Redis status..." -ForegroundColor Yellow
try {
    $redisResponse = redis-cli.exe ping 2>$null
    if ($redisResponse -eq "PONG") {
        Write-Host "✓ Redis is running and responding" -ForegroundColor Green
        Write-Host "  Both API and Redis are operational" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠ Redis is not running" -ForegroundColor Yellow
    Write-Host "  The API will work without caching (fallback mode)" -ForegroundColor Yellow
    Write-Host "  To set up Redis, see REDIS_SETUP_GUIDE.md" -ForegroundColor Yellow
}

Write-Host ""

