package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);
}
