package ru.yandex.practicum.shopping.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.ShoppingStoreClient;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.ProductPageDto;
import ru.yandex.practicum.interaction.api.enumtype.ProductCategory;
import ru.yandex.practicum.interaction.api.enumtype.QuantityState;
import ru.yandex.practicum.shopping.store.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService productService;

    @Override
    public ProductPageDto getProducts(ProductCategory category,
                                      Integer page,
                                      Integer size,
                                      List<String> sort) {
        return productService.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return productService.getProduct(productId);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return productService.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @Override
    public Boolean setProductQuantityState(
            UUID productId,
            QuantityState quantityState) {
        return productService.setProductQuantityState(productId, quantityState);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        return productService.removeProductFromStore(productId);
    }
}