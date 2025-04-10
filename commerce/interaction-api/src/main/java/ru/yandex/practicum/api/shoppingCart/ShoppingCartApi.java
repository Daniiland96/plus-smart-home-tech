package ru.yandex.practicum.api.shoppingCart;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ShoppingCartDto;

@RequestMapping("/api/v1/shopping-cart")
public interface ShoppingCartApi {
    @PutMapping
    ShoppingCartDto addProductInShoppingCart(@RequestParam(name = "username") String userName,
                                             @Valid @RequestBody ShoppingCartDto shoppingCartDto);
}
