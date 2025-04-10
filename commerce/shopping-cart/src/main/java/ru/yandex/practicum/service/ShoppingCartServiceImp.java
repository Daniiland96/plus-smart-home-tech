package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.repository.ShoppingCartRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImp implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;

    @Override
    public ShoppingCartDto addProductInShoppingCart(String userName, ShoppingCartDto shoppingCartDto) {
        return null;
    }
}
