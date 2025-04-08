package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shoppingStore.ShoppingStoreApi;
import ru.yandex.practicum.dto.ProductDto;

@Slf4j
@RestController
public class ShoppingStoreController implements ShoppingStoreApi {

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("DTO на создаие нового продукта: {}", productDto.toString());
        return productDto;
    }
}