-- Reset Flyway migration history for fresh start
DELETE FROM public.flyway_schema_history WHERE version >= 8;

-- Drop problematic columns if they exist
ALTER TABLE sam.products DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.purchases DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS sale_price CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS purchased_price CASCADE;

-- Clear tables
TRUNCATE TABLE sam.sales CASCADE;
TRUNCATE TABLE sam.purchases CASCADE;
TRUNCATE TABLE sam.products CASCADE;

