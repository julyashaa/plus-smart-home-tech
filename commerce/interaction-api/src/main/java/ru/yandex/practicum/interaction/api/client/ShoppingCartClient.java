package ru.yandex.practicum.interaction.api.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductsToShoppingCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Long> products
    );

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromShoppingCart(
            @RequestParam String username,
            @RequestBody List<UUID> products
    );

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest request
    );
}