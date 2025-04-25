package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAssembledOrderException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.feignClient.PaymentFeign;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseFeignClient warehouseFeign;
    private final PaymentFeign paymentFeign;

    @Override
    public OrderDto createOrder(String username, CreateNewOrderRequest newOrderRequest) {
        if (newOrderRequest == null) {
            throw new IllegalArgumentException("CreateNewOrderRequest не должен быть null");
        }
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username не должен быть null или пустым");
        }
        BookedProductsDto bookedProductsDto = warehouseFeign.checkProductQuantityInWarehouse(newOrderRequest.getShoppingCart());
        log.info("Проверка товара на складе: {}", bookedProductsDto);

        Order order = OrderMapper.mapToOrder(username, newOrderRequest, bookedProductsDto);
        Double productCost = paymentFeign.calculateProductCost(OrderMapper.mapToOrderDto(order));
        log.info("Стоимость продуктов в заказе: {}", productCost);
        order.setProductPrice(productCost);

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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id: " + orderId));
        log.info("Получаем заказ из БД: {}", order);
        if (!(order.getState().equals(OrderState.ASSEMBLED) || order.getState().equals(OrderState.PAYMENT_FAILED))) {
            throw new NotAssembledOrderException("Заказа еще не собран на складе");
        }

        PaymentDto paymentDto = paymentFeign.createPaymentOrder(OrderMapper.mapToOrderDto(order));
        log.info("Создали платеж в платежном сервисе: {}", paymentDto);
        order.setState(OrderState.ON_PAYMENT);
        order.setPaymentId(paymentDto.getPaymentId());
        order = orderRepository.save(order);
        log.info("Обновляем статус заказа в БД: {}", order);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        if (returnRequest.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Список возвращаемых продуктов не должен быть пустым");
        }
        Order order = orderRepository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id: " + returnRequest.getOrderId()));
        order.setState(OrderState.PRODUCT_RETURNED);
        order = orderRepository.save(order);
        log.info("Обновляем статус заказа в БД: {}", order);

        warehouseFeign.returnProductsToTheWarehouse(returnRequest.getProducts());
        log.info("Возвращаем продукты на склад");
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto setPaymentFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id: " + orderId));
        order.setState(OrderState.PAYMENT_FAILED);
        order = orderRepository.save(order);
        log.info("Обновляем статус заказа в БД: {}", order);
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id: " + orderId));
        log.info("Находим нужный заказ: {}", order);
        if (order.getDeliveryPrice() == null || order.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных для рассчета полной стоимости заказа");
        }
        Double totalCost = paymentFeign.calculateTotalCost(OrderMapper.mapToOrderDto(order));
        log.info("Полная стоимость заказа: {}", totalCost);
        order.setTotalPrice(totalCost);
        order = orderRepository.save(order);
        log.info("Обновляем заказ в БД: {}", order);
        return OrderMapper.mapToOrderDto(order);
        надо проверить
    }
}