package ru.yandex.practicum.interaction.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SortDto {

    private String direction;

    private String property;
}