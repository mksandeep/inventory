$baseUrl = "http://localhost:8080/api"

Write-Host "Inserting sample data..." -ForegroundColor Green

# Sample product data
$product1 = @{
    productDescription = "Laptop Computer Dell XPS 13"
    sku = "SKU-001"
    dimensions = "30x20x2"
    category = "Electronics"
    aisleNumber = 1
    bayNumber = 1
    startingInventory = 50
    lowStockThreshold = 10
    unitCost = 800.00
    markup = 0.25
    retailPrice = 1000.00
    currentStockLevel = 35
    inventoryValue = 28000.00
    expiryDate = "2027-12-31"
}

$json = $product1 | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/products" -Method POST -Body $json -ContentType "application/json" -ErrorAction SilentlyContinue
Write-Host "Inserted: Laptop Computer" -ForegroundColor Green

Write-Host "Sample data insertion complete!" -ForegroundColor Green
Write-Host "API: http://localhost:8080" -ForegroundColor Yellow
Write-Host "UI: http://localhost:3000" -ForegroundColor Yellow

