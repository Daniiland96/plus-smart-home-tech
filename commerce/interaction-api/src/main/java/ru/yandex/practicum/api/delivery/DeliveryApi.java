package ru.yandex.practicum.api.delivery;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

public interface DeliveryApi {
    @PutMapping("/api/v1/delivery")
    DeliveryDto createDelivery(@Valid @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/api/v1/delivery/cost")
    Double calculateDelivery(@Valid @RequestBody OrderDto orderDto);

}
