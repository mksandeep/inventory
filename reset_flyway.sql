-- Reset the database for fresh migrations
-- First, drop columns if they exist
ALTER TABLE sam.products DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.purchases DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS sale_price CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS purchased_price CASCADE;

-- Drop migrations from Flyway history for versions 8 and later
DELETE FROM public.flyway_schema_history WHERE version >= 8;

-- Commit changes
COMMIT;

