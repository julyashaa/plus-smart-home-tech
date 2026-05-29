package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Long newQuantity;
}