package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseProduct {

    @Id
    private UUID productId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dimension_id")
    private Dimension dimension;

    private Double weight;

    private Boolean fragile;

    private Long quantity;
}