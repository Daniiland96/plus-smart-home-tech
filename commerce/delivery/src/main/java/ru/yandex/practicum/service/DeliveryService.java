package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    Double calculateDelivery(OrderDto orderDto);

    void setDeliverySuccessful(UUID deliveryId);

    void setDeliveryFailed(UUID deliveryId);

    void pickOrderForDelivery(UUID deliveryId);
}
