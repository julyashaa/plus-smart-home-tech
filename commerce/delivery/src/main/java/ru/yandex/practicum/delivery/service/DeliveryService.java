package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    BigDecimal deliveryCost(OrderDto orderDto);

    void pickup(UUID deliveryId);

    void deliverySuccess(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);
}