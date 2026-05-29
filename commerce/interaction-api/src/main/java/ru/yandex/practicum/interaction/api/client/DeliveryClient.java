package ru.yandex.practicum.interaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody OrderDto orderDto);

    @PostMapping("/picked")
    void pickup(@RequestBody UUID deliveryId);

    @PostMapping("/successful")
    void deliverySuccess(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID deliveryId);
}