package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewOrderRequest {
    @NotNull
    @Valid
    private ShoppingCartDto shoppingCart;

    @NotNull
    @Valid
    private AddressDto deliveryAddress;
}