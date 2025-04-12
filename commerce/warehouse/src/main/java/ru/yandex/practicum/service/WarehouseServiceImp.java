package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exeption.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exeption.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exeption.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.feignClient.ShoppingStoreFeignClient;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
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
    private final ShoppingStoreFeignClient storeFeignClient;
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

        checkAvailabilityProductsInWarehouse(productsInCart.keySet(), warehouseProductsMap.keySet()); // проверка наличия продуктов на складе
        checkQuantity(productsInCart, warehouseProductsMap); // проверка количества продуктов на складе

        return bookingProducts(warehouseProductsList);
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

    private void checkAvailabilityProductsInWarehouse(Set<UUID> productsInCart, Set<UUID> productsInWarehouse) {
        productsInCart.removeAll(productsInWarehouse);
        if (!productsInCart.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("На складе нет продуктов со следующими id: " + productsInCart);
        }
    }

    private void checkQuantity(Map<UUID, Integer> productsInCart, Map<UUID, WarehouseProduct> warehouseProductsMap) {
        List<UUID> notAvailabilityProducts = new ArrayList<>();
        for (UUID id : productsInCart.keySet()) {
            if (productsInCart.get(id) < warehouseProductsMap.get(id).getQuantity()) {
                notAvailabilityProducts.add(id);
            }
        }
        if (notAvailabilityProducts.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("На складе не хватает продуктов со следующими id: "
                    + notAvailabilityProducts);
        }
    }

    private BookedProductsDto bookingProducts(List<WarehouseProduct> products) {
        BookedProductsDto result = new BookedProductsDto(0.0, 0.0, false);
        for (WarehouseProduct product : products) {
            result.setDeliveryVolume(result.getDeliveryVolume() + product.getWeight());
            result.setDeliveryVolume(result.getDeliveryVolume()
                    + product.getWidth() * product.getHeight() * product.getDepth());
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
}