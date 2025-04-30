package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProductToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void addProductInWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest);

    AddressDto getAddressWarehouse();

    BookedProductsDto assemblingProductsForOrder(AssemblyProductsForOrderRequest assemblyRequest);

    void returnProductsToWarehouse(Map<UUID, Integer> returnedProducts);

    void shippedProductsToWarehouse(ShippedToDeliveryRequest deliveryRequest);
}
