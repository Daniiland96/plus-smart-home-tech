package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    Double calculateDelivery(OrderDto orderDto);
}
