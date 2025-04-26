package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPaymentOrder(OrderDto orderDto);

    Double calculateProductCost(OrderDto orderDto);

    Double calculateTotalCost(OrderDto orderDto);

    void setPaymentFailed(UUID paymentId);

    void payOrder(UUID paymentId);
}
