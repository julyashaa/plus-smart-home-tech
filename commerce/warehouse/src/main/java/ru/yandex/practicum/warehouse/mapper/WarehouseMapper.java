package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct toEntity(NewProductInWarehouseRequest request);
}