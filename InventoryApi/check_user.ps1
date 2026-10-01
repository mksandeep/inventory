#!/usr/bin/env pwsh

# Database connection parameters
$dbHost = "localhost"
$dbPort = 5432
$dbName = "inventory_db"
$dbUser = "sandeep"
$dbPassword = "sandeep"

# Install or use psql
$psqlPath = "C:\Program Files\PostgreSQL\16\bin\psql.exe"

if (-not (Test-Path $psqlPath)) {
    Write-Host "PostgreSQL client not found. Trying with PATH..."
    $psqlPath = "psql"
}

# SQL query
$sqlQuery = @"
SELECT user_id, email, password, full_name, active, created_at, updated_at FROM sam.users WHERE email = 'admin@example.com';
"@

Write-Host "Checking if admin user exists in database..."
Write-Host ""

& $env:PGPASSWORD="$dbPassword" $psqlPath -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "$sqlQuery"

