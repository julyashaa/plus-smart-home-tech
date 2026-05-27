package ru.yandex.practicum.shopping.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.exception.BadRequestException;
import ru.yandex.practicum.shopping.cart.exception.UnauthorizedException;
import ru.yandex.practicum.shopping.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.shopping.cart.model.ShoppingCart;
import ru.yandex.practicum.shopping.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart cart = getOrCreateCart(username);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addProductsToShoppingCart(String username, Map<UUID, Long> products) {
        checkUsername(username);

        ShoppingCart cart = getOrCreateCart(username);
        checkCartIsActive(cart);

        Map<UUID, Long> updatedProducts = new HashMap<>(cart.getProducts());

        products.forEach((productId, quantity) ->
                updatedProducts.merge(productId, quantity, Long::sum)
        );

        ShoppingCartDto cartForCheck = ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(updatedProducts)
                .build();

        try {
            warehouseClient.checkProductQuantityEnoughForShoppingCart(cartForCheck);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Warehouse service is unavailable. Please try again later."
            );
        }

        cart.setProducts(updatedProducts);

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public void deactivateShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart cart = getOrCreateCart(username);
        cart.setActive(false);
        shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto removeProductsFromShoppingCart(String username, List<UUID> products) {
        checkUsername(username);
        ShoppingCart cart = getOrCreateCart(username);
        checkCartIsActive(cart);

        if (!cart.getProducts().keySet().containsAll(products)) {
            throw new BadRequestException("Product not found in shopping cart");
        }

        products.forEach(cart.getProducts()::remove);

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checkUsername(username);

        ShoppingCart cart = getOrCreateCart(username);
        checkCartIsActive(cart);
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new RuntimeException("Product not found in shopping cart");
        }

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    private ShoppingCart getOrCreateCart(String username) {
        return shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> shoppingCartRepository.save(
                        ShoppingCart.builder()
                                .username(username)
                                .products(new HashMap<>())
                                .active(true)
                                .build()
                ));
    }

    private void checkCartIsActive(ShoppingCart cart) {
        if (!cart.getActive()) {
            throw new BadRequestException("Shopping cart is deactivated");
        }
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new UnauthorizedException("Username must not be blank");
        }
    }
}