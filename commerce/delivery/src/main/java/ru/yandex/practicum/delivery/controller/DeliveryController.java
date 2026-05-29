package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.api.client.DeliveryClient;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {

    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }

    @Override
    public void pickup(UUID deliveryId) {
        deliveryService.pickup(deliveryId);
    }

    @Override
    public void deliverySuccess(UUID deliveryId) {
        deliveryService.deliverySuccess(deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        deliveryService.deliveryFailed(deliveryId);
    }
}