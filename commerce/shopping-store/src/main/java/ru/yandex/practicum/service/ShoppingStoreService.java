package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    ProductDto createProduct(ProductDto productDto);

    ProductDto findProductById(String productId);

    List<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProductFromStore(UUID productId);

    Boolean SetProductQuantityState(SetProductQuantityStateRequest quantityStateRequest);
}
