package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    private String username;

    @Column(name = "shopping_cart_id", nullable = false)
    private UUID shoppingCartId;

    @ElementCollection
//    @CollectionTable(name = "products_in_shopping_carts", joinColumns = @JoinColumn(name = "shopping_cart_id"))
//    @MapKeyColumn(name = "product_id")
//    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;
}
