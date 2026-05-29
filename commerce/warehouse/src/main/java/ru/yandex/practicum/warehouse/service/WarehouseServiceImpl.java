package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.*;
import ru.yandex.practicum.warehouse.exception.WarehouseBadRequestException;
import ru.yandex.practicum.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[new Random(new SecureRandom().nextInt()).nextInt(ADDRESSES.length)];

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new WarehouseBadRequestException("Product already exists in warehouse");
        }
        WarehouseProduct product = warehouseMapper.toEntity(request);
        warehouseRepository.save(product);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new WarehouseBadRequestException("Product not found in warehouse"));

        long currentQuantity = product.getQuantity() == null
                ? 0
                : product.getQuantity();

        product.setQuantity(currentQuantity + request.getQuantity());

        warehouseRepository.save(product);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(
            ShoppingCartDto shoppingCartDto) {
        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (var entry : shoppingCartDto.getProducts().entrySet()) {

            WarehouseProduct product = warehouseRepository.findById(entry.getKey())
                    .orElseThrow(() -> new WarehouseBadRequestException("Product not found in warehouse"));

            long requestedQuantity = entry.getValue();

            if (product.getQuantity() < requestedQuantity) {
                throw new WarehouseBadRequestException("Not enough products in warehouse");
            }

            totalWeight += product.getWeight() * requestedQuantity;

            totalVolume +=
                    product.getDimension().getWidth()
                            * product.getDimension().getHeight()
                            * product.getDimension().getDepth()
                            * requestedQuantity;

            if (Boolean.TRUE.equals(product.getFragile())) {
                fragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(
            AssemblyProductsForOrderRequest request
    ) {
        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (var entry : request.getProducts().entrySet()) {
            WarehouseProduct product = warehouseRepository.findById(entry.getKey())
                    .orElseThrow(() -> new WarehouseBadRequestException("Product not found in warehouse"));

            long requestedQuantity = entry.getValue();

            if (product.getQuantity() < requestedQuantity) {
                throw new WarehouseBadRequestException("Not enough products in warehouse");
            }

            product.setQuantity(product.getQuantity() - requestedQuantity);

            totalWeight += product.getWeight() * requestedQuantity;

            totalVolume += product.getDimension().getWidth()
                    * product.getDimension().getHeight()
                    * product.getDimension().getDepth()
                    * requestedQuantity;

            if (Boolean.TRUE.equals(product.getFragile())) {
                fragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        // Пока нечего обновлять: отдельной сущности OrderBooking у нас ещё нет.
        // Метод нужен по контракту, чтобы delivery мог вызвать warehouse.
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> products) {
        for (var entry : products.entrySet()) {
            WarehouseProduct product = warehouseRepository.findById(entry.getKey())
                    .orElseThrow(() -> new WarehouseBadRequestException("Product not found in warehouse"));

            long currentQuantity = product.getQuantity() == null ? 0 : product.getQuantity();
            product.setQuantity(currentQuantity + entry.getValue());
        }
    }
}