-- Fix Flyway Migration Checksum Mismatch
-- This script repairs the flyway_schema_history table

-- Connect to inventory_db and run this

-- Drop and recreate the database to fix migration issues
-- First, let's check the current state
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- If you need to repair specific migration, run:
-- UPDATE flyway_schema_history SET checksum = 1712352995 WHERE version = 4;

-- Or completely reset Flyway (removes all migration history):
-- DELETE FROM flyway_schema_history WHERE version >= 4;

-- Then restart the application and it will re-run migrations


