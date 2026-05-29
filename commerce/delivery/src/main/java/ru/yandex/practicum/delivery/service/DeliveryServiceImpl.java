package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.api.client.OrderClient;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.interaction.api.enumtype.DeliveryState;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private static final BigDecimal BASE_COST = BigDecimal.valueOf(5.0);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {

        Delivery delivery = Delivery.builder()
                .orderId(deliveryDto.getOrderId())
                .fromAddress(deliveryMapper.toAddress(deliveryDto.getFromAddress()))
                .toAddress(deliveryMapper.toAddress(deliveryDto.getToAddress()))
                .deliveryState(DeliveryState.CREATED)
                .build();

        return deliveryMapper.toDto(
                deliveryRepository.save(delivery));
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        BigDecimal result = BASE_COST;

        String warehouseAddress = warehouseClient.getWarehouseAddress().toString();

        if (warehouseAddress.contains("ADDRESS_2")) {
            result = result.add(BASE_COST.multiply(BigDecimal.valueOf(2)));
        } else {
            result = result.add(BASE_COST);
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            result = result.add(result.multiply(BigDecimal.valueOf(0.2)));
        }

        if (orderDto.getDeliveryWeight() != null) {
            result = result.add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(BigDecimal.valueOf(0.3)));
        }

        if (orderDto.getDeliveryVolume() != null) {
            result = result.add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(BigDecimal.valueOf(0.2)));
        }

        return result;
    }

    @Override
    @Transactional
    public void pickup(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        warehouseClient.shippedToDelivery(
                ShippedToDeliveryRequest.builder()
                        .orderId(delivery.getOrderId())
                        .deliveryId(delivery.getDeliveryId())
                        .build()
        );

        orderClient.assembly(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void deliverySuccess(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.delivery(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryFailed(delivery.getOrderId());
    }

    private Delivery getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found: " + deliveryId));
    }
}