package ru.yandex.practicum.collector.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.collector.dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubEventMapStructMapper {
    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro toAvro(DeviceRemovedEvent event);

    ScenarioAddedEventAvro toAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event);

    ScenarioConditionAvro toAvro(ScenarioCondition condition);

    DeviceActionAvro toAvro(DeviceAction action);

    default DeviceTypeAvro map(DeviceType type) {
        return type == null ? null : DeviceTypeAvro.valueOf(type.name());
    }

    default ConditionTypeAvro map(ConditionType type) {
        return type == null ? null : ConditionTypeAvro.valueOf(type.name());
    }

    default ConditionOperationAvro map(ConditionOperation operation) {
        return operation == null ? null : ConditionOperationAvro.valueOf(operation.name());
    }

    default ActionTypeAvro map(ActionType type) {
        return type == null ? null : ActionTypeAvro.valueOf(type.name());
    }
}
