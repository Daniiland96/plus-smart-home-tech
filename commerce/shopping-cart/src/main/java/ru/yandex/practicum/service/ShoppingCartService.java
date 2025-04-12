package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto addProductInShoppingCart(String userName, ShoppingCartDto shoppingCartDto);
}
