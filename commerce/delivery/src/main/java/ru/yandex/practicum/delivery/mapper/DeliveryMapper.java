package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.model.DeliveryAddress;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "country", source = "country")
    DeliveryAddress toAddress(AddressDto addressDto);

    AddressDto toDto(DeliveryAddress address);
}