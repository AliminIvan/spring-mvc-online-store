CREATE TABLE customers
(
    customer_id    UUID PRIMARY KEY,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    contact_number VARCHAR(20)  NOT NULL
);

CREATE TABLE products
(
    product_id        UUID PRIMARY KEY,
    name              VARCHAR(255)   NOT NULL,
    description       TEXT,
    price             DECIMAL(15, 2) NOT NULL,
    quantity_in_stock INT            NOT NULL
);

CREATE TABLE orders
(
    order_id         UUID PRIMARY KEY,
    order_date       DATE           NOT NULL,
    total_price      DECIMAL(15, 2) NOT NULL,
    order_status     VARCHAR(20)    NOT NULL,
    shipping_address VARCHAR(255)   NOT NULL,
    customer_id      UUID           NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
);

CREATE TABLE order_products
(
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE
);

