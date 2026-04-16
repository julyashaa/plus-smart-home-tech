package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
public class HubEventAvroMapper {
    public HubEventAvro mapToAvro(HubEvent event) {
        HubEventAvro avro = new HubEventAvro();
        avro.setHubId(event.getHubId());
        avro.setTimestamp(toInstant(event.getTimestamp()));

        if (event instanceof DeviceAddedEvent deviceAddedEvent) {
            DeviceAddedEventAvro payload = new DeviceAddedEventAvro();
            payload.setId(deviceAddedEvent.getId());
            payload.setType(mapDeviceType(deviceAddedEvent.getDeviceType()));
            avro.setPayload(payload);

        } else if (event instanceof DeviceRemovedEvent deviceRemovedEvent) {
            DeviceRemovedEventAvro payload = new DeviceRemovedEventAvro();
            payload.setId(deviceRemovedEvent.getId());
            avro.setPayload(payload);

        } else if (event instanceof ScenarioAddedEvent scenarioAddedEvent) {
            ScenarioAddedEventAvro payload = new ScenarioAddedEventAvro();
            payload.setName(scenarioAddedEvent.getName());
            payload.setConditions(mapConditions(scenarioAddedEvent.getConditions()));
            payload.setActions(mapActions(scenarioAddedEvent.getActions()));
            avro.setPayload(payload);

        } else if (event instanceof ScenarioRemovedEvent scenarioRemovedEvent) {
            ScenarioRemovedEventAvro payload = new ScenarioRemovedEventAvro();
            payload.setName(scenarioRemovedEvent.getName());
            avro.setPayload(payload);

        } else {
            throw new IllegalArgumentException("Unknown hub event type: " + event.getClass().getName());
        }

        return avro;
    }

    private List<ScenarioConditionAvro> mapConditions(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(this::mapCondition)
                .toList();
    }

    private ScenarioConditionAvro mapCondition(ScenarioCondition condition) {
        ScenarioConditionAvro avro = new ScenarioConditionAvro();
        avro.setSensorId(condition.getSensorId());
        avro.setType(mapConditionType(condition.getType()));
        avro.setOperation(mapConditionOperation(condition.getOperation()));
        avro.setValue(condition.getValue());
        return avro;
    }

    private List<DeviceActionAvro> mapActions(List<DeviceAction> actions) {
        return actions.stream()
                .map(this::mapAction)
                .toList();
    }

    private DeviceActionAvro mapAction(DeviceAction action) {
        DeviceActionAvro avro = new DeviceActionAvro();
        avro.setSensorId(action.getSensorId());
        avro.setType(mapActionType(action.getType()));
        avro.setValue(action.getValue());
        return avro;
    }

    private DeviceTypeAvro mapDeviceType(DeviceType type) {
        return DeviceTypeAvro.valueOf(type.name());
    }

    private ConditionTypeAvro mapConditionType(ConditionType type) {
        return ConditionTypeAvro.valueOf(type.name());
    }

    private ConditionOperationAvro mapConditionOperation(ConditionOperation operation) {
        return ConditionOperationAvro.valueOf(operation.name());
    }

    private ActionTypeAvro mapActionType(ActionType type) {
        return ActionTypeAvro.valueOf(type.name());
    }

    private Instant toInstant(Instant timestamp) {
        return timestamp != null ? timestamp : Instant.now();
    }
}
