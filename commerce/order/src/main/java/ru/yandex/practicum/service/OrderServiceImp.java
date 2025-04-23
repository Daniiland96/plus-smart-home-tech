package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAssembledOrderException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.feignClient.PaymentFeign;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseFeignClient warehouseClient;
    private final PaymentFeign paymentFeign;

    @Override
    public OrderDto createOrder(String username, CreateNewOrderRequest newOrderRequest) {
        if (newOrderRequest == null) {
            throw new IllegalArgumentException("CreateNewOrderRequest не должен быть null");
        }
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username не должен быть null или пустым");
        }
        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityInWarehouse(newOrderRequest.getShoppingCart());
        log.info("Проверка товара на складе: {}", bookedProductsDto);

        Order order = OrderMapper.mapToOrder(username, newOrderRequest, bookedProductsDto);
        order = orderRepository.save(order);
        log.info("Сохранили заказ в БД: {}", order);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    public List<OrderDto> getUserOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username не должен быть null или пустым");
        }
        List<Order> orders = orderRepository.findAllByUsername(username);
        log.info("Заказы пользователя: {}", orders);

        return OrderMapper.mapToOrderDto(orders);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId не должен быть null");
        }
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new NoOrderFoundException("Заказ с id:" + orderId + " не найден");
        }
        Order order = orderOpt.get();
        log.info("Получаем заказ из БД: {}", order);
        if (!(order.getState().equals(OrderState.ASSEMBLED) || order.getState().equals(OrderState.PAYMENT_FAILED))) {
            throw new NotAssembledOrderException("Заказа еще не собран на складе");
        }

        PaymentDto paymentDto = paymentFeign.createPaymentOrder(OrderMapper.mapToOrderDto(order));
        log.info("Создали платеж в платежном сервисе: {}", paymentDto);
        order.setState(OrderState.ON_PAYMENT);
        order.setPaymentId(paymentDto.getPaymentId());
        order = orderRepository.save(order);
        log.info("Обновляем статус заказа: {}", order);
        return OrderMapper.mapToOrderDto(order);
    }
}