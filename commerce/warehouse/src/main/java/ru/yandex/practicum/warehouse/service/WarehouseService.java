package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interaction.api.dto.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> products);
}