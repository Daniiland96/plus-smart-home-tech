package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.delivery.DeliveryApi;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.service.DeliveryService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        log.info("Запрос на создание доставки: {}", deliveryDto);
        return deliveryService.createDelivery(deliveryDto);
    }
}
