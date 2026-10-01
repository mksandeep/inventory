# Test the API and show current status
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "INVENTORY SYSTEM - VERIFICATION TEST" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"

# Test 1: Check API connection
Write-Host "`n1. Testing API Connection..." -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri "$baseUrl/products" -Method GET -ErrorAction Stop
    Write-Host "   ✓ API is responding" -ForegroundColor Green
} catch {
    Write-Host "   ✗ API is not responding" -ForegroundColor Red
    exit 1
}

# Test 2: Get products count
Write-Host "`n2. Checking Products..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/products" -Method GET
    $products = $response.Content | ConvertFrom-Json
    Write-Host "   ✓ Found $($products.Count) products" -ForegroundColor Green
    if ($products.Count -gt 0) {
        Write-Host "     Sample product: $($products[0].productDescription)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "   ✗ Error fetching products: $_" -ForegroundColor Red
}

# Test 3: Get sales count
Write-Host "`n3. Checking Sales..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/sales" -Method GET
    $sales = $response.Content | ConvertFrom-Json
    Write-Host "   ✓ Found $($sales.Count) sales records" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Error fetching sales: $_" -ForegroundColor Red
}

# Test 4: Get purchases count
Write-Host "`n4. Checking Purchases..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/purchases" -Method GET
    $purchases = $response.Content | ConvertFrom-Json
    Write-Host "   ✓ Found $($purchases.Count) purchase records" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Error fetching purchases: $_" -ForegroundColor Red
}

# Test 5: Check analytics endpoints
Write-Host "`n5. Testing Analytics Endpoints..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/products/total-value" -Method GET
    $value = $response.Content
    Write-Host "   ✓ Total Inventory Value: $value" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Error getting inventory value: $_" -ForegroundColor Red
}

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/sales/revenue-by-quarter" -Method GET
    $quarterly = $response.Content | ConvertFrom-Json
    Write-Host "   ✓ Revenue by Quarter endpoint working ($($quarterly.Count) quarters)" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Error getting quarterly revenue: $_" -ForegroundColor Red
}

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/purchases/total-spent-by-quarter" -Method GET
    $quarterly = $response.Content | ConvertFrom-Json
    Write-Host "   ✓ Purchase analytics endpoint working" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Error getting purchase analytics: $_" -ForegroundColor Red
}

# Test 6: Check UI
Write-Host "`n6. Checking UI Server..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -Method GET -ErrorAction Stop
    Write-Host "   ✓ UI is responding on port 3000" -ForegroundColor Green
} catch {
    Write-Host "   ✗ UI is not responding" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "SYSTEM STATUS SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "API: http://localhost:8080" -ForegroundColor Green
Write-Host "UI:  http://localhost:3000" -ForegroundColor Green
Write-Host "`nTo access the application:" -ForegroundColor Yellow
Write-Host "  1. Open your browser" -ForegroundColor Cyan
Write-Host "  2. Navigate to http://localhost:3000" -ForegroundColor Cyan
Write-Host "  3. Browse the inventory dashboard" -ForegroundColor Cyan
Write-Host "`n========================================" -ForegroundColor Cyan

