CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    customer_id varchar(100),
    total_price numeric,
    created_at  timestamp
)