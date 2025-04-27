package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.feignClient.OrderFeignClient;
import ru.yandex.practicum.feignClient.ShoppingStoreFeignClient;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImp implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository bookingRepository;
    private final ShoppingStoreFeignClient storeFeignClient;
    private final OrderFeignClient orderFeignClient;
    private AddressDto warehouseAddress = settingAddress();

    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        UUID productId = UUID.fromString(newProductInWarehouseRequest.getProductId());
        if (warehouseRepository.findById(productId).isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт с id: " + productId + " уже есть на складе");
        }
        WarehouseProduct newProduct = WarehouseMapper.mapToWarehouseProduct(newProductInWarehouseRequest);
        log.info("Сохраняем в БД новый продукт");
        warehouseRepository.save(newProduct);
    }

    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Integer> productsInCart = shoppingCartDto.getProducts();
        List<WarehouseProduct> warehouseProductsList = warehouseRepository.findAllById(productsInCart.keySet());
        log.info("Продукты из корзины имеющиеся на складе: {}", warehouseProductsList);
        Map<UUID, WarehouseProduct> warehouseProductsMap = warehouseProductsList.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        log.info("Создаем Map из продуктов имеющиеся на складе: {}", warehouseProductsMap);

        checkAvailabilityProductsInWarehouse(productsInCart.keySet(), warehouseProductsMap.keySet()); // проверка наличия продуктов на складе
        checkQuantity(productsInCart, warehouseProductsMap); // проверка количества продуктов на складе

        return bookingProducts(productsInCart, warehouseProductsMap);
    }

    @Override
    public void addProductInWarehouse(AddProductToWarehouseRequest addProductRequest) {
        UUID productId = UUID.fromString(addProductRequest.getProductId());
        WarehouseProduct product = warehouseRepository.findById(productId).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Продукта с id: " + productId + " нет на складе"));
        log.info("Продукт из склада: {}", product);
        product.setQuantity(product.getQuantity() + addProductRequest.getQuantity());
        product = warehouseRepository.save(product);
        log.info("Обновленный продукт: {}", product);
        setProductQuantityState(product);
    }

    @Override
    public AddressDto getAddressWarehouse() {
        return warehouseAddress;
    }

    @Override
    public BookedProductsDto assemblingProductsForTheOrder(AssemblyProductsForOrderRequest assemblyRequest) {
        if (assemblyRequest == null) {
            throw new IllegalArgumentException("AssemblyProductsForOrderRequest не должен быть null");
        }
        Map<UUID, Integer> productsInRequest = assemblyRequest.getProducts();
        log.info("Продукты из AssemblyProductsForOrderRequest: {}", productsInRequest);

        List<WarehouseProduct> warehouseProductsList = warehouseRepository.findAllById(productsInRequest.keySet());
        log.info("Продукты из заказа имеющиеся на складе: {}", warehouseProductsList);
        Map<UUID, WarehouseProduct> warehouseProductsMap = warehouseProductsList.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        log.info("Создаем Map из продуктов имеющиеся на складе: {}", warehouseProductsMap);

        try {
            checkAvailabilityProductsInWarehouse(productsInRequest.keySet(), warehouseProductsMap.keySet()); // проверка наличия продуктов на складе
            checkQuantity(productsInRequest, warehouseProductsMap); // проверка количества продуктов на складе
        } catch (NoSpecifiedProductInWarehouseException | ProductInShoppingCartLowQuantityInWarehouse e) {
            orderFeignClient.assembleOrderFailed(assemblyRequest.getOrderId());
            throw e;
        }

        List<WarehouseProduct> products = changeQuantityProductsInWarehouse(productsInRequest, warehouseProductsMap);
        setProductQuantityState(products);

        BookedProductsDto bookedDto = bookingProducts(productsInRequest, warehouseProductsMap);
        OrderBooking orderBooking = WarehouseMapper.mapToOrderBooking(assemblyRequest, bookedDto);
        orderBooking = bookingRepository.save(orderBooking);
        log.info("Сохраняем бронирование в БД: {}", orderBooking);
        return bookedDto;
    }

    @Override
    public void returnProductsToTheWarehouse(Map<UUID, Integer> returnedProducts) {
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            throw new IllegalArgumentException("Маппа товаров на возврат не должна быть null или пустой");
        }
        List<WarehouseProduct> warehouseProductsList = warehouseRepository.findAllById(returnedProducts.keySet());
        log.info("Продукты из заказа имеющиеся на складе: {}", warehouseProductsList);
        Map<UUID, WarehouseProduct> warehouseProductsMap = warehouseProductsList.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        log.info("Создаем Map из продуктов имеющиеся на складе: {}", warehouseProductsMap);

        checkAvailabilityProductsInWarehouse(returnedProducts.keySet(), warehouseProductsMap.keySet());

        for (UUID id : returnedProducts.keySet()) {
            Integer returnedQuantity = returnedProducts.get(id);
            if (returnedQuantity == null || returnedQuantity <= 0) {
                throw new IllegalArgumentException("Возвращаемое количество товара, должно быть не null, меньше или равно нолю");
            }
            WarehouseProduct product = warehouseProductsMap.get(id);
            product.setQuantity(product.getQuantity() + returnedQuantity);
            log.info("Вернули товар: {}", product);
        }

        List<WarehouseProduct> products = warehouseRepository.saveAll(warehouseProductsMap.values());
        log.info("Обновленные товары: {}", products);

        setProductQuantityState(products);
    }

    @Override
    public void shippedProductsToTheWarehouse(ShippedToDeliveryRequest deliveryRequest) {
        OrderBooking orderBooking = bookingRepository.findById(deliveryRequest.getOrderId()).orElseThrow(() ->
                new NoOrderFoundException("Не найдено бронирование с указанным id заказа: " + deliveryRequest.getOrderId()));
        log.info("Старый OrderBooking: {}", orderBooking);
        orderBooking.setDeliveryId(deliveryRequest.getDeliveryId());
        orderBooking = bookingRepository.save(orderBooking);
        log.info("Обновляем OrderBooking в БД: {}", orderBooking);
    }

    private AddressDto settingAddress() {
        String[] addresses = new String[]{"ADDRESS_1", "ADDRESS_2"};
        String currentAddresses = addresses[Random.from(new SecureRandom()).nextInt(0, 1)];

        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(currentAddresses);
        addressDto.setCity(currentAddresses);
        addressDto.setStreet(currentAddresses);
        addressDto.setHouse(currentAddresses);
        addressDto.setFlat(currentAddresses);
        return addressDto;
    }

    private void checkAvailabilityProductsInWarehouse(Set<UUID> productsInRequest, Set<UUID> productsInWarehouse) {
        Set<UUID> products = new HashSet<>(productsInRequest);
        products.removeAll(productsInWarehouse);
        log.info("Продукты которых нет на складе: {}", products);
        if (!products.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("На складе нет продуктов со следующими id: " + products);
        }
    }

    private void checkQuantity(Map<UUID, Integer> productsInRequest, Map<UUID, WarehouseProduct> warehouseProductsMap) {
        List<UUID> notAvailabilityProducts = new ArrayList<>();
        for (UUID id : productsInRequest.keySet()) {
            if (productsInRequest.get(id) > warehouseProductsMap.get(id).getQuantity()) {
                notAvailabilityProducts.add(id);
            }
        }
        log.info("Продукты которых не хватает на складе: {}", notAvailabilityProducts);
        if (!notAvailabilityProducts.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("На складе не хватает продуктов со следующими id: "
                    + notAvailabilityProducts);
        }
    }

    private BookedProductsDto bookingProducts(
            Map<UUID, Integer> productsInRequest,
            Map<UUID, WarehouseProduct> warehouseProductsMap) {
        BookedProductsDto result = new BookedProductsDto(0.0, 0.0, false);
        for (UUID id : productsInRequest.keySet()) {
            Integer quantity = productsInRequest.get(id);
            WarehouseProduct product = warehouseProductsMap.get(id);

            result.setDeliveryWeight(product.getWeight() * quantity + result.getDeliveryWeight());
            result.setDeliveryVolume(product.getDepth() * product.getWidth() * product.getHeight() * quantity
                    + result.getDeliveryVolume());
            if (product.getFragile()) {
                result.setFragile(true);
            }
        }
        log.info("Общие данные о заказе: {}", result);
        return result;
    }

    private void setProductQuantityState(WarehouseProduct product) {
        Integer quantity = product.getQuantity();
        QuantityState quantityState;
        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (quantity < 10) {
            quantityState = QuantityState.FEW;
        } else if (quantity < 100) {
            quantityState = QuantityState.ENOUGH;
        } else {
            quantityState = QuantityState.MANY;
        }
        storeFeignClient.setProductQuantityState(new SetProductQuantityStateRequest(product.getProductId(), quantityState));
        log.info("Обновление количества продукта в ShoppingStore");
    }

    private void setProductQuantityState(List<WarehouseProduct> products) {
        products.forEach(this::setProductQuantityState);
    }

    private List<WarehouseProduct> changeQuantityProductsInWarehouse(
            Map<UUID, Integer> productsInRequest,
            Map<UUID, WarehouseProduct> warehouseProductsMap) {
        for (UUID id : productsInRequest.keySet()) {
            Integer newQuantity = warehouseProductsMap.get(id).getQuantity() - productsInRequest.get(id);
            warehouseProductsMap.get(id).setQuantity(newQuantity);
            log.info("Задаем новое кличество продукта: {}", warehouseProductsMap.get(id));
        }
        List<WarehouseProduct> productList = warehouseRepository.saveAll(warehouseProductsMap.values());
        log.info("Продукты с обновленным количеством на складе: {}", productList);
        return productList;
    }
}