package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.feignClient.OrderFeignClient;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderFeignClient orderFeign;
    private final WarehouseFeignClient warehouseFeign;

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = DeliveryMapper.mapToDelivery(deliveryDto);
        Optional<Delivery> deliveryOpt = deliveryRepository.findByOrderId(deliveryDto.getOrderId());
        if (deliveryOpt.isPresent()) {
            log.info("Старый Delivery: {}", deliveryOpt.get());
            delivery.setDeliveryId(deliveryOpt.get().getDeliveryId());
        } else {
            delivery.setDeliveryId(null);
        }
        delivery.setDeliveryState(DeliveryState.CREATED);
        delivery = deliveryRepository.save(delivery);
        log.info("Сохраняем доставку в БД: {}", delivery);
        return DeliveryMapper.mapToDeliveryDto(delivery);
    }


}