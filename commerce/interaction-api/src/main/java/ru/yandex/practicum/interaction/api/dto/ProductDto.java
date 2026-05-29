package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.api.enumtype.ProductCategory;
import ru.yandex.practicum.interaction.api.enumtype.ProductState;
import ru.yandex.practicum.interaction.api.enumtype.QuantityState;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    private ProductCategory productCategory;

    @NotNull
    @Min(1)
    private Double price;
}
