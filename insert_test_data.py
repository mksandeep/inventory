#!/usr/bin/env python3
"""
Insert test data into PostgreSQL inventory database for pagination testing
"""

import psycopg2
import sys
from datetime import datetime

# Database connection details
DB_HOST = "localhost"
DB_PORT = 5432
DB_NAME = "inventory_db"
DB_USER = "sandeep"
DB_PASSWORD = "sandeep"

def connect_database():
    """Connect to PostgreSQL database"""
    try:
        conn = psycopg2.connect(
            host=DB_HOST,
            port=DB_PORT,
            database=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD
        )
        return conn
    except psycopg2.Error as e:
        print(f"❌ Database connection error: {e}")
        sys.exit(1)

def read_sql_file(filename):
    """Read SQL file and split into individual statements"""
    with open(filename, 'r') as f:
        content = f.read()
    # Split by semicolon (simple approach)
    statements = [stmt.strip() for stmt in content.split(';') if stmt.strip()]
    return statements

def execute_sql(conn, statements):
    """Execute SQL statements"""
    cursor = conn.cursor()
    try:
        for i, statement in enumerate(statements, 1):
            # Skip comments and empty statements
            if not statement or statement.startswith('--'):
                continue
            
            print(f"Executing statement {i}...")
            cursor.execute(statement)
        
        conn.commit()
        print(f"\n✅ Successfully executed {len(statements)} statements")
        return True
    except psycopg2.Error as e:
        print(f"❌ Error executing SQL: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def main():
    print("=" * 50)
    print("Inserting Test Data into Inventory Database")
    print("=" * 50)
    print(f"Host: {DB_HOST}")
    print(f"Port: {DB_PORT}")
    print(f"Database: {DB_NAME}")
    print(f"User: {DB_USER}")
    print("")
    
    # Connect to database
    print("Connecting to database...")
    conn = connect_database()
    print("✅ Connected successfully")
    print("")
    
    # Read and execute SQL
    sql_file = "C:\\Users\\mksan\\git\\inventory\\insert_test_data.sql"
    print(f"Reading SQL file: {sql_file}")
    statements = read_sql_file(sql_file)
    print(f"Found {len(statements)} SQL statements")
    print("")
    
    print("Executing SQL statements...")
    success = execute_sql(conn, statements)
    
    if success:
        print("")
        print("Summary:")
        print("  • 80 Products inserted (Electronics, Furniture, Office Supplies, Cleaning)")
        print("  • 60 Purchases inserted")
        print("  • 60 Sales inserted")
        print("  • Total: 200 records")
        print("")
        print("✅ Ready for pagination testing!")
    
    conn.close()

if __name__ == "__main__":
    main()

