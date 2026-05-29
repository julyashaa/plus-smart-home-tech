package ru.yandex.practicum.shopping.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.ShoppingCartClient;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductsToShoppingCart(String username,
                                                     Map<UUID, Long> products) {
        return shoppingCartService.addProductsToShoppingCart(username, products);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        shoppingCartService.deactivateShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeProductsFromShoppingCart(String username,
                                                          List<UUID> products) {
        return shoppingCartService.removeProductsFromShoppingCart(username, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username,
                                                 ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}