package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shoppingCart.ShoppingCartApi;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartApi {
    private final ShoppingCartService cartService;

    @Override
    public ShoppingCartDto addProductInShoppingCart(String username, Map<UUID, Integer> productsMap) {
        log.info("Запрос на добавление продуктов в корзину пользователя. UserName: {}, Продукты: {}", username, productsMap);
        return cartService.addProductInShoppingCart(username, productsMap);
    }

    @Override
    public ShoppingCartDto getUserShoppingCart(String username) {
        log.info("Запрос на получение корзины для пользователя с именем: {}", username);
        return cartService.getUserShoppingCart(username);
    }

    @Override
    public void deactivateUserShoppingCart(String username) {
        log.info("Запрос на деактивацию корзины для пользователя с именем: {}", username);
        cartService.deactivateUserShoppingCart(username);
    }
}
