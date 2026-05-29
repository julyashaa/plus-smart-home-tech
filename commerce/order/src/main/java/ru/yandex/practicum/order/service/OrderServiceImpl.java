package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.DeliveryClient;
import ru.yandex.practicum.interaction.api.client.PaymentClient;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.*;
import ru.yandex.practicum.interaction.api.enumtype.DeliveryState;
import ru.yandex.practicum.interaction.api.enumtype.OrderState;
import ru.yandex.practicum.order.exception.NoOrderFoundException;
import ru.yandex.practicum.order.exception.NotAuthorizedUserException;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.getShoppingCart();

        Order order = Order.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getProducts())
                .state(OrderState.NEW)
                .build();

        Order savedOrder = orderRepository.save(order);

        DeliveryDto delivery = deliveryClient.planDelivery(
                DeliveryDto.builder()
                        .orderId(savedOrder.getOrderId())
                        .fromAddress(warehouseClient.getWarehouseAddress())
                        .toAddress(request.getDeliveryAddress())
                        .deliveryState(DeliveryState.CREATED)
                        .build()
        );

        savedOrder.setDeliveryId(delivery.getDeliveryId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);
        warehouseClient.acceptReturn(request.getProducts());
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrder(orderId);
        OrderDto dto = orderMapper.toDto(order);

        order.setProductPrice(paymentClient.productCost(dto));
        order.setTotalPrice(paymentClient.getTotalCost(orderMapper.toDto(order)));

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrder(orderId);
        OrderDto dto = orderMapper.toDto(order);

        order.setDeliveryPrice(deliveryClient.deliveryCost(dto));

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = getOrder(orderId);

        BookedProductsDto booked = warehouseClient.assemblyProductForOrderFromShoppingCart(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(order.getOrderId())
                        .products(order.getProducts())
                        .build()
        );

        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.getFragile());
        order.setState(OrderState.ASSEMBLED);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(order);
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found: " + orderId));
    }
}