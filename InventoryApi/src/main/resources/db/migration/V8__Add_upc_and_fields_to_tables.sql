-- Drop columns if they exist (for safety during cleanup)
ALTER TABLE sam.products DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.purchases DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS upc CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS sale_price CASCADE;
ALTER TABLE sam.sales DROP COLUMN IF EXISTS purchased_price CASCADE;

-- Clear out bad data first
TRUNCATE TABLE sam.sales CASCADE;
TRUNCATE TABLE sam.purchases CASCADE;
TRUNCATE TABLE sam.products CASCADE;

-- Reset sequences to 1
SELECT setval('sam.products_product_id_seq', 1, false);
SELECT setval('sam.purchases_purchase_id_seq', 1, false);
SELECT setval('sam.sales_sale_id_seq', 1, false);

-- Add UPC column to products table
ALTER TABLE sam.products ADD COLUMN upc VARCHAR(255);
ALTER TABLE sam.products ALTER COLUMN upc SET NOT NULL;
ALTER TABLE sam.products ADD CONSTRAINT uk_products_upc UNIQUE (upc);

-- Add UPC column to purchases table
ALTER TABLE sam.purchases ADD COLUMN upc VARCHAR(255);
ALTER TABLE sam.purchases ALTER COLUMN upc SET NOT NULL;

-- Add UPC column to sales table
ALTER TABLE sam.sales ADD COLUMN upc VARCHAR(255);
ALTER TABLE sam.sales ALTER COLUMN upc SET NOT NULL;

-- Add sale_price column to sales table
ALTER TABLE sam.sales ADD COLUMN sale_price DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- Add purchased_price column to sales table
ALTER TABLE sam.sales ADD COLUMN purchased_price DECIMAL(10,2) NOT NULL DEFAULT 0.00;


