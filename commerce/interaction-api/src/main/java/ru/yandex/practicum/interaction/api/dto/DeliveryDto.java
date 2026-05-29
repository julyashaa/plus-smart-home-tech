package ru.yandex.practicum.interaction.api.dto;

import lombok.*;
import ru.yandex.practicum.interaction.api.enumtype.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private UUID deliveryId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private UUID orderId;
    private DeliveryState deliveryState;
}