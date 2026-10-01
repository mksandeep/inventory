CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    product_description VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    dimensions VARCHAR(100),
    category VARCHAR(100) NOT NULL,
    aisle_number INT NOT NULL,
    bay_number INT NOT NULL,
    starting_inventory INT NOT NULL,
    low_stock_threshold INT NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    markup DECIMAL(5,2) NOT NULL,
    retail_price DECIMAL(10,2),
    current_stock_level INT NOT NULL,
    inventory_value DECIMAL(15,2),
    expiry_date DATE
);

CREATE TABLE purchases (
    purchase_id BIGSERIAL PRIMARY KEY,
    order_reference_no VARCHAR(100) NOT NULL,
    product_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    vendor VARCHAR(100) NOT NULL,
    units_ordered INT NOT NULL,
    cost_per_unit DECIMAL(10,2) NOT NULL,
    total_purchase_amount DECIMAL(15,2),
    aisle_number INT NOT NULL,
    bay_number INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE sales (
    sale_id BIGSERIAL PRIMARY KEY,
    invoice_no VARCHAR(100) NOT NULL,
    product_id BIGINT NOT NULL,
    transaction_date DATE NOT NULL,
    ship_date DATE,
    customer_name VARCHAR(100) NOT NULL,
    units_sold INT NOT NULL,
    retail_price DECIMAL(10,2) NOT NULL,
    total_sales_amount DECIMAL(15,2),
    sell_date DATE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
