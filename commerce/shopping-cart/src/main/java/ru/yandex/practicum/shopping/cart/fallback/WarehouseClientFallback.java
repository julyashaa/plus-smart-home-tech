package ru.yandex.practicum.shopping.cart.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.*;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseClientFallback implements WarehouseClient {

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        throw new IllegalStateException("Warehouse service is unavailable");
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        throw new IllegalStateException("Warehouse service is unavailable");
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        throw new IllegalStateException("Warehouse service is unavailable. Please try again later.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new IllegalStateException("Warehouse service is unavailable");
    }

    @Override
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        throw new IllegalStateException("Warehouse service is unavailable");
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        throw new IllegalStateException("Warehouse service is unavailable");
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        throw new IllegalStateException("Warehouse service is unavailable");
    }
}