create TABLE IF NOT EXISTS shopping_carts
(
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_name VARCHAR,
    cart_state VARCHAR
);

create TABLE IF NOT EXISTS products_in_shopping_carts
(
    shopping_cart_id UUID,
    product_id UUID,
    quantity INTEGER
);