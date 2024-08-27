INSERT INTO customers (customer_id, first_name, last_name, email, contact_number)
VALUES ('d290f1ee-6c54-4b01-90e6-d701748f0851', 'John', 'Doe', 'john.doe@example.com', '1234567890'),
       ('d290f1ee-6c54-4b01-90e6-d701748f0852', 'Jane', 'Smith', 'jane.smith@example.com', '0987654321');

INSERT INTO products (product_id, name, description, price, quantity_in_stock)
VALUES ('8a30b774-90e1-4f5d-8c4a-9b041c9e14d1', 'Laptop', 'High performance laptop', 999.99, 50),
       ('8a30b774-90e1-4f5d-8c4a-9b041c9e14d2', 'Smartphone', 'Latest model smartphone', 799.99, 100);

INSERT INTO orders (order_id, order_date, total_price, order_status, shipping_address, customer_id)
VALUES ('c290f1ee-6c54-4b01-90e6-d701748f0851', '2024-08-01', 1799.98, 'IN_PROGRESS', '123 Main St, Springfield',
        'd290f1ee-6c54-4b01-90e6-d701748f0851');

INSERT INTO order_products (order_id, product_id)
VALUES ('c290f1ee-6c54-4b01-90e6-d701748f0851', '8a30b774-90e1-4f5d-8c4a-9b041c9e14d1'),
       ('c290f1ee-6c54-4b01-90e6-d701748f0851', '8a30b774-90e1-4f5d-8c4a-9b041c9e14d2');

