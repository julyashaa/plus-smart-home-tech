package ru.yandex.practicum.interaction.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPageDto {

    private Long totalElements;

    private Integer totalPages;

    private Integer numberOfElements;

    private Boolean first;

    private Boolean last;

    private Integer size;

    private List<ProductDto> content;

    private Integer number;

    private List<SortDto> sort;

    private Boolean empty;
}