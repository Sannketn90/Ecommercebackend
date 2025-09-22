-- ===== CREATE CART TABLE =====
CREATE TABLE carts (
    cart_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    total_price DOUBLE PRECISION DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ===== CREATE CART_ITEM TABLE =====
CREATE TABLE cart_items (
    item_id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER CHECK (quantity > 0),
    price_snapshot DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);