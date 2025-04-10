package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto addProductInShoppingCart(String userName, ShoppingCartDto shoppingCartDto);
}
