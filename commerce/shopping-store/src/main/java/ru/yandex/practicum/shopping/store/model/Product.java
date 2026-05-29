package ru.yandex.practicum.shopping.store.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction.api.enumtype.ProductCategory;
import ru.yandex.practicum.interaction.api.enumtype.ProductState;
import ru.yandex.practicum.interaction.api.enumtype.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    private String productName;

    @Column(length = 5000)
    private String description;

    private String imageSrc;

    private Double price;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    private ProductState productState;
}
