# Fix Flyway Migration Checksum Mismatch
# This script drops and recreates the inventory_db database

Write-Host "╔═════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  Fixing Flyway Migration Checksum Mismatch                 ║" -ForegroundColor Green
Write-Host "║  This will DROP and recreate the database                  ║" -ForegroundColor Yellow
Write-Host "╚═════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

# Confirm with user
$response = Read-Host "This will DELETE all data. Continue? (yes/no)"
if ($response -ne "yes") {
    Write-Host "Cancelled." -ForegroundColor Red
    exit
}

Write-Host "`n⏳ Connecting to PostgreSQL..." -ForegroundColor Cyan

# Drop and recreate database
$sql = @"
-- Drop existing database
DROP DATABASE IF EXISTS inventory_db;

-- Create fresh database
CREATE DATABASE inventory_db;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO sandeep;
"@

# Execute using psql
$sql | psql -U postgres -h localhost

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✅ Database recreated successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Kill any running API process"
    Write-Host "2. Start the API:"
    Write-Host "   cd C:\Users\mksan\git\inventory\InventoryApi"
    Write-Host "   java -jar build/libs/InventoryApi-1.0-SNAPSHOT.jar"
    Write-Host ""
    Write-Host "The API will now re-run all migrations and start successfully." -ForegroundColor Cyan
} else {
    Write-Host "`n❌ Error dropping database!" -ForegroundColor Red
    Write-Host "Make sure PostgreSQL is running and you have the right credentials." -ForegroundColor Yellow
}


