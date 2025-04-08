package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.mapper.ShoppingStoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImp implements ShoppingStoreService {
    private final ShoppingStoreRepository storeRepository;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (productDto.getProductId() != null) {
            throw new IllegalArgumentException("При создании нового продукта, полу productId должно быть null");
        }
        Product product = ShoppingStoreMapper.mapToProduct(productDto);
    }
}
