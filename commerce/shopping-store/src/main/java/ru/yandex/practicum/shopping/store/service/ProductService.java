package ru.yandex.practicum.shopping.store.service;

import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.ProductPageDto;
import ru.yandex.practicum.interaction.api.enumtype.ProductCategory;
import ru.yandex.practicum.interaction.api.enumtype.QuantityState;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductPageDto getProducts(ProductCategory category, Integer page, Integer size, List<String> sort);

    ProductDto getProduct(UUID productId);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean setProductQuantityState(UUID productId,
                                    QuantityState quantityState);

    Boolean removeProductFromStore(UUID productId);
}