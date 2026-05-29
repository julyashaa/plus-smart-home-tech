package ru.yandex.practicum.delivery.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}