package ru.yandex.practicum.api.shoppingStore;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;


@RequestMapping("/api/v1/shopping-store")
public interface ShoppingStoreApi {

    @PutMapping
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto);

    @GetMapping("/{productId}")
    ProductDto findProductById(@PathVariable("productId") String productId);

    @GetMapping
    List<ProductDto> findAllByProductCategory(
            @RequestParam(name = "category") ProductCategory productCategory,
            Pageable pageable);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    Boolean SetProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest quantityStateRequest);
}
