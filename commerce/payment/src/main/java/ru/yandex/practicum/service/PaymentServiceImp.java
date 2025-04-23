package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.PaymentState;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.feignClient.OrderFeign;
import ru.yandex.practicum.feignClient.ShoppingStoreFeign;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.PaymentEntity;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderFeign orderFeign;
    private final ShoppingStoreFeign storeFeign;

    @Override
    public PaymentDto createPaymentOrder(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == null || orderDto.getDeliveryPrice() == null || orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных для оплаты заказа");
        }
        PaymentEntity paymentEntity = PaymentMapper.mapToPaymentEntity(orderDto);
        Optional<PaymentEntity> oldPaymentEntityOpt = paymentRepository.findByOrderId(orderDto.getOrderId());
        if (oldPaymentEntityOpt.isPresent()) {
            log.info("Старая сущность PaymentEntity: {}", oldPaymentEntityOpt.get());
              paymentEntity.setPaymentId(oldPaymentEntityOpt.get().getPaymentId());
        }

        paymentEntity.setPaymentState(PaymentState.PENDING);
        paymentEntity = paymentRepository.save(paymentEntity);
        log.info("Сохранили платеж в БД: {}", paymentEntity);

        return PaymentMapper.mapToPaymentDto(paymentEntity);
    }
}