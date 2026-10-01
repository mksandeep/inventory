# Insert test data into inventory database
# This script connects to PostgreSQL and runs the SQL script with 100+ test records

param (
    [string]$DBHost = "localhost",
    [int]$DBPort = 5432,
    [string]$Database = "inventory_db",
    [string]$Username = "sandeep",
    [string]$Password = "sandeep"
)

$sqlFile = "C:\Users\mksan\git\inventory\insert_test_data.sql"
$psqlPath = "C:\Program Files\PostgreSQL\15\bin\psql.exe"  # Adjust path if needed

Write-Host "======================================" -ForegroundColor Green
Write-Host "Inserting Test Data into Inventory DB" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""
Write-Host "Host: $DBHost"
Write-Host "Port: $DBPort"
Write-Host "Database: $Database"
Write-Host "User: $Username"
Write-Host ""

# Check if psql exists
if (-not (Test-Path $psqlPath)) {
    Write-Host "ERROR: psql not found at $psqlPath" -ForegroundColor Red
    Write-Host "Please install PostgreSQL or update the psqlPath variable" -ForegroundColor Red
    exit 1
}

# Check if SQL file exists
if (-not (Test-Path $sqlFile)) {
    Write-Host "ERROR: SQL file not found at $sqlFile" -ForegroundColor Red
    exit 1
}

# Set environment variable for password
$env:PGPASSWORD = $Password

try {
    Write-Host "Connecting to database and executing SQL script..." -ForegroundColor Cyan

    # Run psql with SQL file
    & $psqlPath -h $DBHost -p $DBPort -U $Username -d $Database -f $sqlFile

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Test data inserted successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Summary:" -ForegroundColor Green
        Write-Host "  • 80 Products inserted (Electronics, Furniture, Office Supplies, Cleaning)"
        Write-Host "  • 60 Purchases inserted"
        Write-Host "  • 60 Sales inserted"
        Write-Host "  • Total: 200 records"
        Write-Host ""
        Write-Host "Ready for pagination testing!" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "❌ Error inserting test data" -ForegroundColor Red
        exit 1
    }
} finally {
    # Clear password from environment
    Remove-Item env:PGPASSWORD -ErrorAction SilentlyContinue
}


