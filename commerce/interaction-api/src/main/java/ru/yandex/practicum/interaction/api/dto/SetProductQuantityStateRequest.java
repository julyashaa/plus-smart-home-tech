package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.api.enumtype.QuantityState;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetProductQuantityStateRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}