package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.feignClient.OrderFeignClient;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.AddressRepository;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
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
            log.info("Доставки для указанного заказа нет, создаем новую");
            delivery.setDeliveryId(null);
        }

        delivery.setFromAddress(findOrCreateAddress(delivery.getFromAddress()));
        if (checkEqualsAddress(delivery.getFromAddress(), delivery.getToAddress())) {
            delivery.setToAddress(delivery.getFromAddress());
            log.info("Адрес склада и адрес доставки совпадают");
        } else {
            delivery.setToAddress(findOrCreateAddress(delivery.getToAddress()));
        }

        delivery.setDeliveryState(DeliveryState.CREATED);
        delivery = deliveryRepository.save(delivery);
        log.info("Сохраняем доставку в БД: {}", delivery);
        return DeliveryMapper.mapToDeliveryDto(delivery);
    }

    @Override
    public Double calculateDelivery(OrderDto orderDto) {
        if (!validOrder(orderDto)) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных для расчета стоимости доставки");
        }
        Delivery delivery = findDeliveryById(orderDto.getDeliveryId());
        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();
        Double cost = 5.0;

        switch (fromAddress.getCity()) {
            case "ADDRESS_1":
                cost += cost;
                break;
            case "ADDRESS_2":
                cost += cost * 2;
                break;
            default:
                break;
        }
        if (orderDto.getFragile()) {
            cost += cost * 0.2;
        }
        cost += orderDto.getDeliveryWeight() * 0.3;
        cost += orderDto.getDeliveryVolume() * 0.2;
        if (!(fromAddress.getCountry().equals(toAddress.getCountry())
                && fromAddress.getCity().equals(toAddress.getCity())
                && fromAddress.getStreet().equals(toAddress.getStreet()))) {
            cost += cost * 0.2;
        }
        log.info("Стоимость доставки: {}", cost);
        return cost;
    }

    @Override
    public void setDeliverySuccessful(UUID deliveryId) {
        sefsef
    }

    @Override
    public void setDeliveryFailed(UUID deliveryId) {
        sefsf
    }

    @Override
    public void pickOrderForDelivery(UUID deliveryId) {
        sefsef
    }

    private Address findOrCreateAddress(Address address) {
        Optional<Address> addressOpt = addressRepository.findByCountryAndCityAndStreetAndHouseAndFlat(
                address.getCountry(), address.getCity(), address.getStreet(), address.getHouse(), address.getFlat());
        if (addressOpt.isPresent()) {
            log.info("Старый адрес: {}", addressOpt.get());
            return addressOpt.get();
        }
        Address result = addressRepository.save(address);
        log.info("Создаем новый адрес: {}", result);
        return result;
    }

    private Boolean checkEqualsAddress(Address a1, Address a2) {
        return Objects.equals(a1.getCountry(), a2.getCountry()) &&
                Objects.equals(a1.getCity(), a2.getCity()) &&
                Objects.equals(a1.getStreet(), a2.getStreet()) &&
                Objects.equals(a1.getHouse(), a2.getHouse()) &&
                Objects.equals(a1.getFlat(), a2.getFlat());
    }

    private Delivery findDeliveryById(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка с id: " + deliveryId));
        log.info("Находим доставку в БД: {}", delivery);
        return delivery;
    }

    private Boolean validOrder(OrderDto dto) {
        return dto.getDeliveryWeight() != null
                && dto.getDeliveryVolume() != null
                && dto.getFragile() != null;
    }
}