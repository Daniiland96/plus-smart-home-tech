package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

public interface PaymentService {
    PaymentDto createPaymentOrder(OrderDto orderDto);
}
