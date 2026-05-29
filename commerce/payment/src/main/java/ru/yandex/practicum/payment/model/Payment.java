package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction.api.enumtype.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private UUID paymentId;

    private UUID orderId;

    private BigDecimal totalPayment;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}