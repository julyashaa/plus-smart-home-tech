package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private Boolean fragile;

    @Valid
    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(1)
    private Double weight;
}