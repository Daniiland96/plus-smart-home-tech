package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderDto {
    @NotNull
    private UUID orderId;

    @NotBlank
    private String username;

    @NotNull
    private UUID shoppingCartId;

    private Map<UUID, Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    @NotNull
    private OrderState state;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    @PositiveOrZero
    private Double totalPrice;

    @PositiveOrZero
    private Double deliveryPrice;

    @PositiveOrZero
    private Double productPrice;
}
