package ru.yandex.practicum.shopping.cart.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.*;

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
}