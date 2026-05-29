package ru.yandex.practicum.shopping.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.client.ShoppingCartClient;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService shoppingCartService;

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProductsToShoppingCart(@RequestParam String username,
                                                     @RequestBody Map<UUID, Long> products) {
        return shoppingCartService.addProductsToShoppingCart(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivateShoppingCart(@RequestParam String username) {
        shoppingCartService.deactivateShoppingCart(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromShoppingCart(@RequestParam String username,
                                                          @RequestBody List<UUID> products) {
        return shoppingCartService.removeProductsFromShoppingCart(username, products);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}