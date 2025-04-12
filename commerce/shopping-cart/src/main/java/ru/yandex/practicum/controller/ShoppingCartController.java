package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shoppingCart.ShoppingCartApi;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartApi {
    private final ShoppingCartService cartService;

    @Override
    public ShoppingCartDto addProductInShoppingCart(String userName, ShoppingCartDto shoppingCartDto) {
        log.info("Запрос на добавление нового продукта в корзину пользователя. UserName: {}, ShoppingCartDto: {}",
                userName, shoppingCartDto);
        return cartService.addProductInShoppingCart(userName, shoppingCartDto);
    }
}
