package ru.yandex.practicum.interaction.api.dto;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {
    private Map<UUID, Long> products;
    private UUID orderId;
}