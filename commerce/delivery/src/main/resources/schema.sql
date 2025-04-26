create TABLE IF NOT EXISTS addresses
(
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country VARCHAR NOT NULL,
    city VARCHAR NOT NULL,
    street VARCHAR NOT NULL,
    house VARCHAR NOT NULL,
    flat VARCHAR
);

create TABLE IF NOT EXISTS deliveries
(
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    delivery_volume DOUBLE PRECISION,
    delivery_weight DOUBLE PRECISION,
    fragile BOOLEAN,
    from_address_id UUID REFERENCES addresses (address_id),
    to_address_id UUID REFERENCES addresses (address_id),
    delivery_state VARCHAR NOT NULL,
    order_id UUID NOT NULL
);