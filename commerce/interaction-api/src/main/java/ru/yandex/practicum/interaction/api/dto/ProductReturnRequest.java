package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReturnRequest {
    private UUID orderId;

    @NotNull
    private Map<UUID, Long> products;
}