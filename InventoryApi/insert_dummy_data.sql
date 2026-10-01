-- Comprehensive dummy data insert script
-- This script inserts sample data for all tables in the inventory system
-- Schema: sam

-- ============================================================================
-- 1. INSERT PRODUCTS
-- ============================================================================
-- Clear existing data (optional - comment out if you want to keep existing data)
-- DELETE FROM sam.sales;
-- DELETE FROM sam.purchases;
-- DELETE FROM sam.products;

INSERT INTO sam.products (
    product_description, sku, dimensions, category, aisle_number, bay_number,
    starting_inventory, low_stock_threshold, unit_cost, markup, retail_price,
    current_stock_level, inventory_value, expiry_date
) VALUES
-- Electronics Category
('Laptop Computer Dell XPS 13', 'SKU-001', '30x20x2', 'Electronics', 1, 1, 50, 10, 800.00, 0.25, 1000.00, 35, 28000.00, '2027-12-31'),
('Wireless Mouse Logitech MX', 'SKU-002', '10x5x3', 'Electronics', 1, 2, 200, 30, 25.00, 0.40, 35.00, 150, 3750.00, '2026-12-31'),
('USB-C Cable 2m', 'SKU-003', '20x5x2', 'Electronics', 1, 3, 500, 100, 3.00, 0.50, 4.50, 320, 960.00, '2028-06-30'),
('Monitor LG 27 inch 4K', 'SKU-004', '60x40x15', 'Electronics', 2, 1, 25, 5, 350.00, 0.30, 455.00, 18, 6300.00, '2027-06-30'),
('Mechanical Keyboard RGB', 'SKU-005', '45x15x3', 'Electronics', 2, 2, 100, 15, 80.00, 0.35, 108.00, 65, 5400.00, '2027-03-31'),

-- Office Supplies Category
('A4 Paper 500 sheets', 'SKU-010', '25x20x5', 'Office Supplies', 3, 1, 1000, 200, 2.50, 0.40, 3.50, 600, 1500.00, '2025-12-31'),
('Ballpoint Pens Box of 50', 'SKU-011', '15x10x8', 'Office Supplies', 3, 2, 500, 100, 1.50, 0.60, 2.40, 350, 525.00, '2026-12-31'),
('Desk Organizer Wooden', 'SKU-012', '30x20x10', 'Office Supplies', 3, 3, 80, 10, 12.00, 0.50, 18.00, 45, 540.00, '2027-12-31'),
('Stapler Heavy Duty', 'SKU-013', '12x8x6', 'Office Supplies', 4, 1, 150, 20, 8.00, 0.50, 12.00, 85, 680.00, '2026-12-31'),
('Sticky Notes Assorted 100 pads', 'SKU-014', '10x10x3', 'Office Supplies', 4, 2, 300, 50, 1.00, 0.50, 1.50, 200, 200.00, '2025-06-30'),

-- Furniture Category
('Office Chair Ergonomic', 'SKU-020', '70x60x90', 'Furniture', 5, 1, 30, 5, 150.00, 0.40, 210.00, 18, 2700.00, '2028-12-31'),
('Desk Table 120x60', 'SKU-021', '120x60x75', 'Furniture', 5, 2, 20, 3, 200.00, 0.35, 270.00, 14, 3780.00, '2028-12-31'),
('Filing Cabinet 4 Drawer', 'SKU-022', '50x40x130', 'Furniture', 5, 3, 15, 3, 180.00, 0.40, 252.00, 9, 2268.00, '2028-12-31'),
('Bookshelf 5 Tier', 'SKU-023', '80x30x200', 'Furniture', 6, 1, 25, 4, 120.00, 0.45, 174.00, 15, 2610.00, '2027-12-31'),
('Conference Table Large', 'SKU-024', '240x120x75', 'Furniture', 6, 2, 8, 1, 800.00, 0.30, 1040.00, 5, 5200.00, '2028-12-31'),

-- Stationery Category
('Scissors Professional', 'SKU-030', '20x10x2', 'Stationery', 7, 1, 200, 30, 5.00, 0.50, 7.50, 120, 600.00, '2026-12-31'),
('Ruler Metal 30cm', 'SKU-031', '35x3x1', 'Stationery', 7, 2, 300, 50, 1.50, 0.60, 2.40, 180, 270.00, '2026-12-31'),
('Correction Fluid Bottle', 'SKU-032', '8x4x10', 'Stationery', 7, 3, 250, 40, 2.00, 0.50, 3.00, 140, 280.00, '2025-12-31'),
('Highlighter Set 6 colors', 'SKU-033', '18x10x3', 'Stationery', 8, 1, 150, 25, 3.00, 0.50, 4.50, 85, 255.00, '2026-06-30'),
('Markers Permanent Set 24', 'SKU-034', '25x15x5', 'Stationery', 8, 2, 120, 20, 4.50, 0.40, 6.30, 70, 315.00, '2025-09-30');

-- ============================================================================
-- 2. INSERT PURCHASES
-- ============================================================================
INSERT INTO sam.purchases (
    order_reference_no, product_id, order_date, expected_delivery_date, vendor,
    units_ordered, cost_per_unit, total_purchase_amount, aisle_number, bay_number
) VALUES
-- Electronics purchases
('PO-2026-001', 1, '2026-01-15', '2026-02-01', 'Dell Direct', 50, 800.00, 40000.00, 1, 1),
('PO-2026-002', 2, '2026-01-20', '2026-02-05', 'Logitech Asia', 200, 25.00, 5000.00, 1, 2),
('PO-2026-003', 3, '2026-02-01', '2026-02-15', 'Cable Supplier Ltd', 500, 3.00, 1500.00, 1, 3),
('PO-2026-004', 4, '2026-01-10', '2026-02-10', 'LG Electronics', 25, 350.00, 8750.00, 2, 1),
('PO-2026-005', 5, '2026-02-05', '2026-02-20', 'Keyboard World', 100, 80.00, 8000.00, 2, 2),

-- Office Supplies purchases
('PO-2026-010', 6, '2026-02-01', '2026-02-08', 'Paper Plus Inc', 1000, 2.50, 2500.00, 3, 1),
('PO-2026-011', 7, '2026-01-25', '2026-02-05', 'Stationery Mart', 500, 1.50, 750.00, 3, 2),
('PO-2026-012', 8, '2026-02-03', '2026-02-15', 'Wooden Goods Co', 80, 12.00, 960.00, 3, 3),
('PO-2026-013', 9, '2026-01-20', '2026-02-02', 'Office Depot', 150, 8.00, 1200.00, 4, 1),
('PO-2026-014', 10, '2026-02-08', '2026-02-15', 'Sticky Notes Pro', 300, 1.00, 300.00, 4, 2),

-- Furniture purchases
('PO-2026-020', 11, '2026-01-05', '2026-01-30', 'Furniture Factory', 30, 150.00, 4500.00, 5, 1),
('PO-2026-021', 12, '2026-01-10', '2026-02-05', 'Desk Maker Ltd', 20, 200.00, 4000.00, 5, 2),
('PO-2026-022', 13, '2026-01-15', '2026-02-10', 'Cabinet Solutions', 15, 180.00, 2700.00, 5, 3),
('PO-2026-023', 14, '2026-02-01', '2026-02-20', 'Shelf Industries', 25, 120.00, 3000.00, 6, 1),
('PO-2026-024', 15, '2025-12-20', '2026-01-15', 'Conference Tables Inc', 8, 800.00, 6400.00, 6, 2),

-- Stationery purchases
('PO-2026-030', 16, '2026-01-18', '2026-02-01', 'Scissors Traders', 200, 5.00, 1000.00, 7, 1),
('PO-2026-031', 17, '2026-02-02', '2026-02-10', 'Ruler Supplies', 300, 1.50, 450.00, 7, 2),
('PO-2026-032', 18, '2026-01-25', '2026-02-08', 'Correction Fluid Corp', 250, 2.00, 500.00, 7, 3),
('PO-2026-033', 19, '2026-02-05', '2026-02-15', 'Highlighter Makers', 150, 3.00, 450.00, 8, 1),
('PO-2026-034', 20, '2026-02-03', '2026-02-12', 'Marker Suppliers', 120, 4.50, 540.00, 8, 2);

-- ============================================================================
-- 3. INSERT SALES
-- ============================================================================
INSERT INTO sam.sales (
    invoice_no, product_id, transaction_date, ship_date, customer_name,
    units_sold, retail_price, total_sales_amount, sell_date
) VALUES
-- Electronics sales (SOLD items)
('INV-2026-001', 1, '2026-03-01', '2026-03-02', 'Tech Corp Ltd', 8, 1000.00, 8000.00, '2026-03-01'),
('INV-2026-002', 1, '2026-03-05', '2026-03-06', 'StartUp Innovations', 5, 1000.00, 5000.00, '2026-03-05'),
('INV-2026-003', 2, '2026-03-10', '2026-03-11', 'Office Works', 12, 35.00, 420.00, '2026-03-10'),
('INV-2026-004', 2, '2026-03-15', '2026-03-16', 'Design Studio Pro', 8, 35.00, 280.00, '2026-03-15'),
('INV-2026-005', 3, '2026-03-08', '2026-03-09', 'Tech Supplies Store', 50, 4.50, 225.00, '2026-03-08'),
('INV-2026-006', 4, '2026-03-12', '2026-03-13', 'Entertainment Hub', 3, 455.00, 1365.00, '2026-03-12'),
('INV-2026-007', 5, '2026-03-18', '2026-03-19', 'Gaming Cafe', 12, 108.00, 1296.00, '2026-03-18'),

-- Office Supplies sales (SOLD items)
('INV-2026-010', 6, '2026-03-02', '2026-03-03', 'Print Center', 150, 3.50, 525.00, '2026-03-02'),
('INV-2026-011', 6, '2026-03-20', '2026-03-21', 'School Board', 100, 3.50, 350.00, '2026-03-20'),
('INV-2026-012', 7, '2026-03-04', '2026-03-05', 'University Admin', 75, 2.40, 180.00, '2026-03-04'),
('INV-2026-013', 8, '2026-03-09', '2026-03-10', 'Home Office Supplies', 18, 18.00, 324.00, '2026-03-09'),
('INV-2026-014', 9, '2026-03-14', '2026-03-15', 'Corporate Gifts', 25, 12.00, 300.00, '2026-03-14'),
('INV-2026-015', 10, '2026-03-17', '2026-03-18', 'Craft Store', 50, 1.50, 75.00, '2026-03-17'),

-- Furniture sales (MIXED - some sold, some unsold)
('INV-2026-020', 11, '2026-02-15', '2026-02-16', 'Office Space Furnishers', 6, 210.00, 1260.00, '2026-02-15'),
('INV-2026-021', 11, '2026-03-10', '2026-03-11', 'Co-Working Spaces', 4, 210.00, 840.00, '2026-03-10'),
('INV-2026-022', 12, '2026-03-01', '2026-03-02', 'Corporate Offices', 4, 270.00, 1080.00, '2026-03-01'),
('INV-2026-023', 13, '2026-03-05', '2026-03-06', 'Law Firm Associates', 2, 252.00, 504.00, '2026-03-05'),

-- Stationery sales (SOLD items)
('INV-2026-030', 16, '2026-03-03', '2026-03-04', 'Art Supplies Store', 30, 7.50, 225.00, '2026-03-03'),
('INV-2026-031', 17, '2026-03-06', '2026-03-07', 'Craft Workshop', 50, 2.40, 120.00, '2026-03-06'),
('INV-2026-032', 18, '2026-03-11', '2026-03-12', 'Correction Supplies', 40, 3.00, 120.00, '2026-03-11'),
('INV-2026-033', 19, '2026-03-13', '2026-03-14', 'Art Classes', 25, 4.50, 112.50, '2026-03-13'),
('INV-2026-034', 20, '2026-03-19', '2026-03-20', 'School Supplies', 35, 6.30, 220.50, '2026-03-19');

-- ============================================================================
-- 4. SUMMARY
-- ============================================================================
-- Total tables updated:
-- - products: 20 records inserted
-- - purchases: 20 records inserted
-- - sales: 21 records inserted

-- ============================================================================
-- 5. VERIFICATION QUERIES
-- ============================================================================
-- Run these queries to verify the data:

-- SELECT 'Products' as table_name, COUNT(*) as total_records FROM sam.products
-- UNION ALL
-- SELECT 'Purchases', COUNT(*) FROM sam.purchases
-- UNION ALL
-- SELECT 'Sales', COUNT(*) FROM sam.sales;

-- To see current stock levels (current_stock_level - units sold):
-- SELECT product_id, product_description, sku, current_stock_level,
--        (SELECT COALESCE(SUM(units_sold), 0) FROM sam.sales WHERE product_id = p.product_id) as total_units_sold,
--        current_stock_level - (SELECT COALESCE(SUM(units_sold), 0) FROM sam.sales WHERE product_id = p.product_id) as remaining_stock
-- FROM sam.products p
-- ORDER BY product_id;

