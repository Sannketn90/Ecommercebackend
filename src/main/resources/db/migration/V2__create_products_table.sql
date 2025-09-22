-- ===== CREATE PRODUCT TABLE =====
CREATE TABLE products (
    product_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    description VARCHAR(500),
    user_id UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_product_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);