-- Flyway Migration: Reset and re-insert sample data

-- Disable foreign key constraint checking
SET CONSTRAINTS ALL DEFERRED;

-- Delete all existing data from dependent tables first
DELETE FROM sam.sales;
DELETE FROM sam.purchases;
DELETE FROM sam.products;

-- Reset sequences to 1
SELECT setval('sam.products_product_id_seq', 1, false);
SELECT setval('sam.purchases_purchase_id_seq', 1, false);
SELECT setval('sam.sales_sale_id_seq', 1, false);

-- Insert fresh dummy products
INSERT INTO sam.products (product_description, upc, sku, dimensions, category, aisle_number, bay_number, starting_inventory, low_stock_threshold, unit_cost, markup, retail_price, current_stock_level, inventory_value, expiry_date, created_by) VALUES
('Laptop Computer Dell XPS 13', 'UPC-001', 'SKU-001', '30x20x2', 'Electronics', 1, 1, 50, 10, 800.00, 0.25, 1000.00, 35, 28000.00, '2027-12-31', 'SYSTEM'),
('Wireless Mouse Logitech', 'UPC-002', 'SKU-002', '10x5x3', 'Electronics', 1, 2, 200, 50, 25.00, 0.40, 35.00, 120, 3000.00, '2027-06-30', 'SYSTEM'),
('USB-C Cable 2m', 'UPC-003', 'SKU-003', '20x5x1', 'Accessories', 1, 3, 500, 100, 5.00, 0.50, 7.50, 280, 1400.00, '2027-12-31', 'SYSTEM'),
('Monitor 24 inch 4K', 'UPC-004', 'SKU-004', '55x30x5', 'Electronics', 2, 1, 30, 8, 300.00, 0.35, 405.00, 18, 5400.00, '2027-09-30', 'SYSTEM'),
('Mechanical Keyboard RGB', 'UPC-005', 'SKU-005', '45x15x3', 'Peripherals', 2, 2, 100, 20, 120.00, 0.30, 156.00, 65, 7800.00, '2027-08-31', 'SYSTEM'),
('Office Chair Ergonomic', 'UPC-006', 'SKU-006', '65x65x100', 'Furniture', 3, 1, 25, 5, 250.00, 0.40, 350.00, 18, 4500.00, '2028-03-31', 'SYSTEM'),
('Standing Desk Electric', 'UPC-007', 'SKU-007', '140x60x80', 'Furniture', 3, 2, 15, 3, 400.00, 0.50, 600.00, 12, 4800.00, '2028-06-30', 'SYSTEM'),
('Webcam HD 1080p', 'UPC-008', 'SKU-008', '8x8x8', 'Electronics', 1, 4, 80, 15, 60.00, 0.45, 87.00, 52, 3120.00, '2027-10-31', 'SYSTEM'),
('Speaker System 2.1', 'UPC-009', 'SKU-009', '60x20x15', 'Audio', 2, 3, 40, 10, 150.00, 0.35, 202.50, 28, 4200.00, '2027-11-30', 'SYSTEM'),
('Laptop Stand Adjustable', 'UPC-010', 'SKU-010', '30x20x5', 'Accessories', 1, 5, 150, 30, 45.00, 0.40, 63.00, 95, 4275.00, '2027-12-31', 'SYSTEM');

-- Insert fresh dummy purchases (orders across Q1, Q2, Q3, Q4 of 2026)
INSERT INTO sam.purchases (order_reference_no, product_id, upc, order_date, expected_delivery_date, vendor, units_ordered, cost_per_unit, total_purchase_amount, aisle_number, bay_number, created_by) VALUES
('PO-2026-001', 1, 'UPC-001', '2026-01-10', '2026-01-20', 'Tech Supplies Inc', 20, 800.00, 16000.00, 1, 1, 'SYSTEM'),
('PO-2026-002', 2, 'UPC-002', '2026-01-15', '2026-01-25', 'Peripherals Ltd', 100, 25.00, 2500.00, 1, 2, 'SYSTEM'),
('PO-2026-003', 3, 'UPC-003', '2026-02-05', '2026-02-15', 'Cable Distributors', 200, 5.00, 1000.00, 1, 3, 'SYSTEM'),
('PO-2026-004', 4, 'UPC-004', '2026-02-20', '2026-03-05', 'Display World', 15, 300.00, 4500.00, 2, 1, 'SYSTEM'),
('PO-2026-005', 5, 'UPC-005', '2026-03-10', '2026-03-20', 'Keyboard Makers', 50, 120.00, 6000.00, 2, 2, 'SYSTEM'),
('PO-2026-006', 6, 'UPC-006', '2026-04-05', '2026-04-15', 'Office Furniture Co', 10, 250.00, 2500.00, 3, 1, 'SYSTEM'),
('PO-2026-007', 7, 'UPC-007', '2026-04-20', '2026-05-05', 'Desk Manufacturers', 8, 400.00, 3200.00, 3, 2, 'SYSTEM'),
('PO-2026-008', 8, 'UPC-008', '2026-05-10', '2026-05-20', 'Camera Supplies', 40, 60.00, 2400.00, 1, 4, 'SYSTEM'),
('PO-2026-009', 9, 'UPC-009', '2026-05-25', '2026-06-05', 'Audio Systems Ltd', 20, 150.00, 3000.00, 2, 3, 'SYSTEM'),
('PO-2026-010', 10, 'UPC-010', '2026-06-15', '2026-06-25', 'Accessory Vendors', 75, 45.00, 3375.00, 1, 5, 'SYSTEM'),
('PO-2026-011', 1, 'UPC-001', '2026-07-05', '2026-07-15', 'Tech Supplies Inc', 15, 800.00, 12000.00, 1, 1, 'SYSTEM'),
('PO-2026-012', 2, 'UPC-002', '2026-07-20', '2026-07-30', 'Peripherals Ltd', 80, 25.00, 2000.00, 1, 2, 'SYSTEM'),
('PO-2026-013', 4, 'UPC-004', '2026-08-10', '2026-08-20', 'Display World', 12, 300.00, 3600.00, 2, 1, 'SYSTEM'),
('PO-2026-014', 5, 'UPC-005', '2026-08-25', '2026-09-05', 'Keyboard Makers', 40, 120.00, 4800.00, 2, 2, 'SYSTEM'),
('PO-2026-015', 6, 'UPC-006', '2026-09-05', '2026-09-15', 'Office Furniture Co', 8, 250.00, 2000.00, 3, 1, 'SYSTEM'),
('PO-2026-016', 7, 'UPC-007', '2026-09-20', '2026-10-05', 'Desk Manufacturers', 6, 400.00, 2400.00, 3, 2, 'SYSTEM'),
('PO-2026-017', 3, 'UPC-003', '2026-10-05', '2026-10-15', 'Cable Distributors', 150, 5.00, 750.00, 1, 3, 'SYSTEM'),
('PO-2026-018', 9, 'UPC-009', '2026-10-20', '2026-10-30', 'Audio Systems Ltd', 15, 150.00, 2250.00, 2, 3, 'SYSTEM'),
('PO-2026-019', 10, 'UPC-010', '2026-11-05', '2026-11-15', 'Accessory Vendors', 60, 45.00, 2700.00, 1, 5, 'SYSTEM'),
('PO-2026-020', 2, 'UPC-002', '2026-11-20', '2026-11-30', 'Peripherals Ltd', 70, 25.00, 1750.00, 1, 2, 'SYSTEM');

-- Insert fresh dummy sales (transactions across Q1, Q2, Q3, Q4 of 2026)
INSERT INTO sam.sales (invoice_no, product_id, upc, transaction_date, ship_date, customer_name, units_sold, purchased_price, sale_price, total_sales_amount, sell_date, created_by) VALUES
-- Q1 Sales
('INV-2026-001', 1, 'UPC-001', '2026-01-15', '2026-01-18', 'John Tech Solutions', 3, 800.00, 1000.00, 3000.00, '2026-01-15', 'SYSTEM'),
('INV-2026-002', 2, 'UPC-002', '2026-01-20', '2026-01-22', 'Creative Agency Inc', 15, 25.00, 35.00, 525.00, '2026-01-20', 'SYSTEM'),
('INV-2026-003', 3, 'UPC-003', '2026-02-05', '2026-02-07', 'StartUp Co', 25, 5.00, 7.50, 187.50, '2026-02-05', 'SYSTEM'),
('INV-2026-004', 4, 'UPC-004', '2026-02-10', '2026-02-12', 'Design Studios', 2, 300.00, 405.00, 810.00, '2026-02-10', 'SYSTEM'),
('INV-2026-005', 5, 'UPC-005', '2026-03-01', '2026-03-03', 'Development Team', 8, 120.00, 156.00, 1248.00, '2026-03-01', 'SYSTEM'),
('INV-2026-006', 6, 'UPC-006', '2026-03-15', '2026-03-17', 'Corporate Office', 3, 250.00, 350.00, 1050.00, '2026-03-15', 'SYSTEM'),
-- Q2 Sales
('INV-2026-007', 7, 'UPC-007', '2026-04-05', '2026-04-07', 'Enterprise Consulting', 2, 400.00, 600.00, 1200.00, '2026-04-05', 'SYSTEM'),
('INV-2026-008', 8, 'UPC-008', '2026-04-20', '2026-04-22', 'Content Creators', 12, 60.00, 87.00, 1044.00, '2026-04-20', 'SYSTEM'),
('INV-2026-009', 9, 'UPC-009', '2026-05-10', '2026-05-12', 'Music Production Ltd', 5, 150.00, 202.50, 1012.50, '2026-05-10', 'SYSTEM'),
('INV-2026-010', 10, 'UPC-010', '2026-05-25', '2026-05-27', 'Tech Startup Hub', 20, 45.00, 63.00, 1260.00, '2026-05-25', 'SYSTEM'),
-- Q3 Sales
('INV-2026-011', 1, 'UPC-001', '2026-07-10', '2026-07-12', 'Large Corp', 5, 800.00, 1000.00, 5000.00, '2026-07-10', 'SYSTEM'),
('INV-2026-012', 2, 'UPC-002', '2026-07-18', '2026-07-20', 'SME Business', 10, 25.00, 35.00, 350.00, '2026-07-18', 'SYSTEM'),
('INV-2026-013', 4, 'UPC-004', '2026-08-05', '2026-08-07', 'Tech Store', 3, 300.00, 405.00, 1215.00, '2026-08-05', 'SYSTEM'),
('INV-2026-014', 5, 'UPC-005', '2026-08-22', '2026-08-24', 'Office Supplies', 6, 120.00, 156.00, 936.00, '2026-08-22', 'SYSTEM'),
('INV-2026-015', 6, 'UPC-006', '2026-09-10', '2026-09-12', 'Retail Partner', 4, 250.00, 350.00, 1400.00, '2026-09-10', 'SYSTEM'),
-- Q4 Sales
('INV-2026-016', 3, 'UPC-003', '2026-10-15', '2026-10-17', 'Bulk Buyer', 80, 5.00, 7.50, 600.00, '2026-10-15', 'SYSTEM'),
('INV-2026-017', 7, 'UPC-007', '2026-10-20', '2026-10-22', 'Office Furnishings', 1, 400.00, 600.00, 600.00, '2026-10-20', 'SYSTEM'),
('INV-2026-018', 9, 'UPC-009', '2026-11-08', '2026-11-10', 'Audio Retailer', 8, 150.00, 202.50, 1620.00, '2026-11-08', 'SYSTEM'),
('INV-2026-019', 10, 'UPC-010', '2026-11-25', '2026-11-27', 'Accessories Store', 30, 45.00, 63.00, 1890.00, '2026-11-25', 'SYSTEM'),
('INV-2026-020', 8, 'UPC-008', '2026-12-05', '2026-12-07', 'Tech Gadgets Co', 15, 60.00, 87.00, 1305.00, '2026-12-05', 'SYSTEM');

