#!/usr/bin/env powershell
# ============================================================================
# Dummy Data Insertion Script for Inventory System
# ============================================================================
# This script helps insert dummy data into the inventory database
#
# Usage: .\insert_dummy_data.ps1
#
# Requirements:
# - PostgreSQL running on localhost:5432
# - Database: inventory_db
# - User: sandeep
# - Password: sandeep
# ============================================================================

param(
    [string]$Host = "localhost",
    [int]$Port = 5432,
    [string]$Database = "inventory_db",
    [string]$User = "sandeep",
    [string]$Password = "sandeep"
)

# Colors for output
$colors = @{
    Success = 'Green'
    Error = 'Red'
    Warning = 'Yellow'
    Info = 'Cyan'
}

function Write-Status {
    param([string]$Message, [ValidateSet('Success', 'Error', 'Warning', 'Info')]$Type = 'Info')
    Write-Host $Message -ForegroundColor $colors[$Type]
}

Write-Status "============================================================================" Info
Write-Status "Inventory System - Dummy Data Insertion Script" Info
Write-Status "============================================================================" Info
Write-Host ""

# Check if psql is available
Write-Status "Checking for PostgreSQL client..." Info
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue

if ($null -eq $psqlPath) {
    Write-Status "❌ ERROR: PostgreSQL client (psql) not found in PATH" Error
    Write-Host "Please install PostgreSQL or add it to your PATH environment variable"
    Write-Host "Download from: https://www.postgresql.org/download/"
    exit 1
}

Write-Status "✅ PostgreSQL client found: $($psqlPath.Source)" Success
Write-Host ""

# Verify database connection
Write-Status "Verifying database connection..." Info
Write-Host "  Host: $Host"
Write-Host "  Port: $Port"
Write-Host "  Database: $Database"
Write-Host "  User: $User"
Write-Host ""

# Create environment variable for password
$env:PGPASSWORD = $Password

try {
    # Test connection
    $testResult = psql -h $Host -p $Port -U $User -d $Database -c "SELECT 1;" 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Status "✅ Database connection successful" Success
    } else {
        throw "Connection failed"
    }
} catch {
    Write-Status "❌ ERROR: Could not connect to database" Error
    Write-Host "Error details: $_"
    Write-Host ""
    Write-Host "Troubleshooting:"
    Write-Host "  1. Ensure PostgreSQL is running"
    Write-Host "  2. Verify database exists: inventory_db"
    Write-Host "  3. Verify user credentials: sandeep / sandeep"
    Write-Host "  4. Check firewall settings"
    exit 1
}

Write-Host ""

# Get the SQL file path
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFilePath = Join-Path $scriptPath "InventoryApi\insert_dummy_data.sql"

if (-not (Test-Path $sqlFilePath)) {
    Write-Status "❌ ERROR: SQL script not found at: $sqlFilePath" Error
    Write-Host "Please ensure insert_dummy_data.sql exists in the InventoryApi directory"
    exit 1
}

Write-Status "✅ SQL script found: $sqlFilePath" Success
Write-Host ""

# Read and display what will be inserted
Write-Status "Data to be inserted:" Info
Write-Host "  - 20 Products (5 categories)"
Write-Host "  - 20 Purchases"
Write-Host "  - 21 Sales records"
Write-Host ""

# Ask for confirmation
Write-Host "⚠️  WARNING: This will INSERT data into your database" -ForegroundColor Yellow
$confirmation = Read-Host "Do you want to proceed? (YES/NO)"

if ($confirmation -ne "YES") {
    Write-Status "❌ Operation cancelled by user" Warning
    exit 0
}

Write-Host ""
Write-Status "Inserting dummy data..." Info
Write-Host ""

# Execute the SQL script
try {
    psql -h $Host -p $Port -U $User -d $Database -f $sqlFilePath

    if ($LASTEXITCODE -eq 0) {
        Write-Status "✅ Dummy data insertion completed successfully!" Success

        # Verify the insertion
        Write-Host ""
        Write-Status "Verifying data insertion..." Info
        Write-Host ""

        $verifyQuery = @"
SELECT
    'Products' as table_name, COUNT(*) as total_records
FROM sam.products
UNION ALL
SELECT 'Purchases', COUNT(*) FROM sam.purchases
UNION ALL
SELECT 'Sales', COUNT(*) FROM sam.sales;
"@

        psql -h $Host -p $Port -U $User -d $Database -c $verifyQuery

        Write-Host ""
        Write-Status "✅ All data inserted successfully!" Success
        Write-Host ""
        Write-Host "Next steps:"
        Write-Host "  1. Start the API: java -jar build/libs/InventoryApi-1.0-SNAPSHOT.jar"
        Write-Host "  2. Test endpoints: http://localhost:8080/api/products"
        Write-Host "  3. Check UI: http://localhost:3000"
        Write-Host ""

    } else {
        Write-Status "❌ ERROR: SQL script execution failed" Error
        exit 1
    }
} catch {
    Write-Status "❌ ERROR: An error occurred during insertion" Error
    Write-Host "Error details: $_"
    exit 1
} finally {
    # Clear the password from environment
    Remove-Item env:PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Status "============================================================================" Info
Write-Status "Script completed!" Success
Write-Status "============================================================================" Info

