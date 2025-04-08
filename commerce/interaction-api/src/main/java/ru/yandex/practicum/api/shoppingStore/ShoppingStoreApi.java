package ru.yandex.practicum.api.shoppingStore;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.dto.ProductDto;

@RequestMapping("/api/v1/shopping-store")
public interface ShoppingStoreApi {

    @PutMapping
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto);
}
