package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dimensions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double width;

    private Double height;

    private Double depth;
}