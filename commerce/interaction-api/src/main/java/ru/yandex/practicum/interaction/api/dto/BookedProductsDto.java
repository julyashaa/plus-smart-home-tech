package ru.yandex.practicum.interaction.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedProductsDto {

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;
}