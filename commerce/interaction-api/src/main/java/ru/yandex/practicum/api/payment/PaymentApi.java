package ru.yandex.practicum.api.payment;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

public interface PaymentApi {
    @PostMapping("/api/v1/payment")
    PaymentDto createPaymentOrder(@Valid @RequestBody OrderDto orderDto);
}
