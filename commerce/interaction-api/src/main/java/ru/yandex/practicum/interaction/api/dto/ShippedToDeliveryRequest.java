package ru.yandex.practicum.interaction.api.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippedToDeliveryRequest {
    private UUID orderId;
    private UUID deliveryId;
}