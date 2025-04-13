package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.ProductState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ShoppingStoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImp implements ShoppingStoreService {
    private final ShoppingStoreRepository storeRepository;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (productDto.getProductId() != null) {
            throw new IllegalArgumentException("При создании нового продукта, поле productId должно быть null");
        }
        Product product = ShoppingStoreMapper.mapToProduct(productDto);
        product = storeRepository.save(product);
        log.info("Сохранили в БД новый product: {}", product);
        return ShoppingStoreMapper.mapToProductDto(product);
    }

    @Override
    public ProductDto findProductById(String productId) {
        UUID id = UUID.fromString(productId);
        log.info("Преобразуем productId в формат UUID: {}", id);
        Product product = storeRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product с id: " + id + " не найден"));
        return ShoppingStoreMapper.mapToProductDto(product);
    }

    @Override
    public List<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable) {
        log.info("Запрос к БД");
        List<Product> products = storeRepository.findAllByProductCategory(productCategory, pageable);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("Не найдено продуктов с категорией: " + productCategory);
        }
        return ShoppingStoreMapper.mapToProductDto(products);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getProductId() == null || productDto.getProductId().isBlank()) {
            throw new IllegalArgumentException("При обновлении продукта, поле productId не должно быть null или пустым");
        }
        UUID productId = UUID.fromString(productDto.getProductId());
        log.info("Id искомого продукта: {}", productId);
        Product productOld = storeRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product с id: " + productId + " не найден"));
        log.info("Получили старый product: {}", productOld);

        Product productNew = ShoppingStoreMapper.mapToProduct(productDto);
        productNew.setProductId(productId);

        Product result = storeRepository.save(productNew);
        log.info("Сохранили новый product: {}", result);
        return ShoppingStoreMapper.mapToProductDto(result);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        Product product = storeRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product с id: " + productId + " не найден"));
        log.info("Получили старый product: {}", product);

        if (!product.getProductState().equals(ProductState.DEACTIVATE)) {
            product.setProductState(ProductState.DEACTIVATE);
            product = storeRepository.save(product);
            log.info("Удаленный product: {}", product);
        }
        return product.getProductState().equals(ProductState.DEACTIVATE);
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest quantityStateRequest) {
        Product product = storeRepository.findById(quantityStateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product с id: "
                        + quantityStateRequest.getProductId() + " не найден"));
        log.info("Получили старый product: {}", product);

        if(!product.getQuantityState().equals(quantityStateRequest.getQuantityState())) {
            product.setQuantityState(quantityStateRequest.getQuantityState());
            product = storeRepository.save(product);
            log.info("Product с обновленным QuantityState: {}", product);
        }
        return product.getQuantityState().equals(quantityStateRequest.getQuantityState());
    }
}