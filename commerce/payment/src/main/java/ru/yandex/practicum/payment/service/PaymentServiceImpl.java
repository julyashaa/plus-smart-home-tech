package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.OrderClient;
import ru.yandex.practicum.interaction.api.client.ShoppingStoreClient;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.enumtype.PaymentState;
import ru.yandex.practicum.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.1);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        return orderDto.getProducts().entrySet()
                .stream()
                .map(entry -> {
                    ProductDto product = shoppingStoreClient.getProduct(entry.getKey());

                    return BigDecimal.valueOf(product.getPrice())
                            .multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        BigDecimal productPrice = orderDto.getProductPrice() != null
                ? orderDto.getProductPrice()
                : productCost(orderDto);

        BigDecimal deliveryPrice = orderDto.getDeliveryPrice() != null
                ? orderDto.getDeliveryPrice()
                : BigDecimal.ZERO;

        BigDecimal tax = productPrice.multiply(VAT_RATE);

        return productPrice
                .add(tax)
                .add(deliveryPrice);
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto orderDto) {
        BigDecimal productPrice = orderDto.getProductPrice() != null
                ? orderDto.getProductPrice()
                : productCost(orderDto);

        BigDecimal deliveryPrice = orderDto.getDeliveryPrice() != null
                ? orderDto.getDeliveryPrice()
                : BigDecimal.ZERO;

        BigDecimal totalPrice = getTotalCost(orderDto);

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(totalPrice)
                .deliveryTotal(deliveryPrice)
                .feeTotal(productPrice)
                .state(PaymentState.PENDING)
                .build();

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setState(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Payment not found: " + paymentId));
    }
}