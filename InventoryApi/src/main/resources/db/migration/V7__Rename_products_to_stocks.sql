-- Add missing columns to sales and purchases to support the schema
-- Add upc column to sales if it doesn't exist
ALTER TABLE sam.sales ADD COLUMN IF NOT EXISTS upc VARCHAR(255);

-- Add upc column to purchases if it doesn't exist
ALTER TABLE sam.purchases ADD COLUMN IF NOT EXISTS upc VARCHAR(255);

-- Note: The Sale and Purchase entities use stockId field which maps to the product_id column in the database
-- This is intentional - Stock is a domain model alias for Product in the inventory system


