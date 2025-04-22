package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(String username, CreateNewOrderRequest newOrderRequest);

    List<OrderDto> getUserOrders(String username);
}
