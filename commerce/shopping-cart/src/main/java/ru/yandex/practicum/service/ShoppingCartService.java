package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto addProductInShoppingCart(String username, Map<UUID, Integer> productsMap);

    ShoppingCartDto getUserShoppingCart(String username);

    void deactivateUserShoppingCart(String username);
}
