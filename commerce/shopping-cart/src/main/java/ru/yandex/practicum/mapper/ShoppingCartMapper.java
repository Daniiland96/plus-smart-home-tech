package ru.yandex.practicum.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Slf4j
public class ShoppingCartMapper {
    public static ShoppingCartDto mapToShoppingCartDto(ShoppingCart shoppingCart) {
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(shoppingCart.getShoppingCartId().toString());
        dto.setProducts(shoppingCart.getProducts());
        log.info("Результат маппинага в ShoppingCartDto: {}", dto);
        return dto;
    }
}
