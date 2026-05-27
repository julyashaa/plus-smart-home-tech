package ru.yandex.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.ProductPageDto;
import ru.yandex.practicum.interaction.api.dto.SortDto;
import ru.yandex.practicum.interaction.api.enumtype.ProductCategory;
import ru.yandex.practicum.interaction.api.enumtype.ProductState;
import ru.yandex.practicum.interaction.api.enumtype.QuantityState;
import ru.yandex.practicum.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.shopping.store.mapper.ProductMapper;
import ru.yandex.practicum.shopping.store.model.Product;
import ru.yandex.practicum.shopping.store.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return productMapper.toDto(product);
    }

    @Override
    public ProductPageDto getProducts(ProductCategory category,
                                      Integer page,
                                      Integer size,
                                      List<String> sort) {

        Sort.Direction direction = Sort.Direction.ASC;
        String property = "productName";

        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.get(0).split(",");

            property = parts[0];

            if (parts.length > 1) {
                direction = Sort.Direction.fromString(parts[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

        Page<ProductDto> productsPage = productRepository
                .findByProductCategoryAndProductState(
                        category,
                        ProductState.ACTIVE,
                        pageable
                )
                .map(productMapper::toDto);

        return ProductPageDto.builder()
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .numberOfElements(productsPage.getNumberOfElements())
                .first(productsPage.isFirst())
                .last(productsPage.isLast())
                .size(productsPage.getSize())
                .content(productsPage.getContent())
                .number(productsPage.getNumber())
                .sort(List.of(
                        SortDto.builder()
                                .direction(direction.name())
                                .property(property)
                                .build()
                ))
                .empty(productsPage.isEmpty())
                .build();
    }

    private List<Sort.Order> parseSort(List<String> sort) {

        if (sort == null || sort.isEmpty()) {
            return List.of(Sort.Order.asc("productName"));
        }

        return sort.stream()
                .map(value -> {
                    String[] parts = value.split(",");

                    String property = parts[0];

                    Sort.Direction direction =
                            parts.length > 1
                                    ? Sort.Direction.fromString(parts[1])
                                    : Sort.Direction.ASC;

                    return new Sort.Order(direction, property);
                })
                .toList();
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productMapper.updateEntity(product, productDto);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public Boolean setProductQuantityState(
            UUID productId,
            QuantityState quantityState) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setQuantityState(quantityState);

        productRepository.save(product);

        return true;
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);

        return true;
    }
}
