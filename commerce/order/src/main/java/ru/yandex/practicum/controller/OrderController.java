package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.order.OrderApi;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {
    private final OrderService orderService;

    @Override
    public OrderDto createOrder(String username, CreateNewOrderRequest newOrderRequest) {
        log.info("Запрос пользователя: {} на создание нового заказа: {}", username, newOrderRequest);
        return orderService.createOrder(username, newOrderRequest);
    }

    @Override
    public List<OrderDto> getUserOrders(String username) {
        log.info("Запрос на получение заказов пользователя: {}", username);
        return orderService.getUserOrders(username);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        log.info("Запрос на оплату заказа: {}", orderId);
        return orderService.payOrder(orderId);
    }
}