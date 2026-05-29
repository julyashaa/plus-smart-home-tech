package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    BigDecimal productCost(OrderDto orderDto);

    BigDecimal getTotalCost(OrderDto orderDto);

    PaymentDto payment(OrderDto orderDto);

    void paymentSuccess(UUID paymentId);

    void paymentFailed(UUID paymentId);
}