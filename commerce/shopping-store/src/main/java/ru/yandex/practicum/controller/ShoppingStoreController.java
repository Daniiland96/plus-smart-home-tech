package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shoppingStore.ShoppingStoreApi;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreApi {
    private final ShoppingStoreService storeService;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("DTO на создаие нового продукта: {}", productDto);
        return storeService.createProduct(productDto);
    }

    @Override
    public ProductDto findProductById(String productId) {
        log.info("Поиск product по его productId: {}", productId);
        return storeService.findProductById(productId);
    }

    @Override
    public List<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable) {
        log.info("Поиск products по каиегории и pageable: {}, {}", productCategory, pageable);
        return storeService.findAllByProductCategory(productCategory, pageable);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("DTO на обновление продукта: {}", productDto);
        return storeService.updateProduct(productDto);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        log.info("Id продукта, который нужно удалить: {}", productId);
        return storeService.removeProductFromStore(productId);
    }

    @Override
    public Boolean SetProductQuantityState(SetProductQuantityStateRequest quantityStateRequest) {
        log.info("Запрос на обновление QuantityState продукта: {}", quantityStateRequest);
        return storeService.SetProductQuantityState(quantityStateRequest);
    }
}