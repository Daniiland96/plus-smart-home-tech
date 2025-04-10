package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.feignClient.ShoppingStoreFeignClient;
import ru.yandex.practicum.repository.ShoppingCartRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImp implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;

    @Override
    public ShoppingCartDto addProductInShoppingCart(String userName, ShoppingCartDto shoppingCartDto) {
        if (userName == null || userName.isBlank()){
            throw new IllegalArgumentException("Имя пользователя не может быть null или пустым");
        }
        if (shoppingCartDto == null) {
            throw new IllegalArgumentException("ShoppingCartDto не может быть null");
        }

        return null;
    }

}
