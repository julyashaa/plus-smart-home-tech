package ru.yandex.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.ActionType;
import ru.yandex.practicum.collector.dto.hub.ConditionOperation;
import ru.yandex.practicum.collector.dto.hub.ConditionType;
import ru.yandex.practicum.collector.dto.hub.DeviceAction;
import ru.yandex.practicum.collector.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.collector.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.collector.dto.hub.DeviceType;
import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.collector.dto.hub.ScenarioCondition;
import ru.yandex.practicum.collector.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

import java.time.Instant;
import java.util.List;

@Component
public class HubEventProtoMapper {

    public HubEvent toDto(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> mapDeviceAdded(proto);
            case DEVICE_REMOVED -> mapDeviceRemoved(proto);
            case SCENARIO_ADDED -> mapScenarioAdded(proto);
            case SCENARIO_REMOVED -> mapScenarioRemoved(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload is not set");
        };
    }

    private DeviceAddedEvent mapDeviceAdded(HubEventProto proto) {
        var payload = proto.getDeviceAdded();

        DeviceAddedEvent event = new DeviceAddedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setId(payload.getId());
        event.setDeviceType(DeviceType.valueOf(payload.getType().name()));
        return event;
    }

    private DeviceRemovedEvent mapDeviceRemoved(HubEventProto proto) {
        var payload = proto.getDeviceRemoved();

        DeviceRemovedEvent event = new DeviceRemovedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setId(payload.getId());
        return event;
    }

    private ScenarioAddedEvent mapScenarioAdded(HubEventProto proto) {
        var payload = proto.getScenarioAdded();

        ScenarioAddedEvent event = new ScenarioAddedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setName(payload.getName());
        event.setConditions(
                payload.getConditionList().stream()
                        .map(this::mapCondition)
                        .toList()
        );
        event.setActions(
                payload.getActionList().stream()
                        .map(this::mapAction)
                        .toList()
        );
        return event;
    }

    private ScenarioRemovedEvent mapScenarioRemoved(HubEventProto proto) {
        var payload = proto.getScenarioRemoved();

        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setName(payload.getName());
        return event;
    }

    private ScenarioCondition mapCondition(ScenarioConditionProto proto) {
        ScenarioCondition condition = new ScenarioCondition();
        condition.setSensorId(proto.getSensorId());
        condition.setType(ConditionType.valueOf(proto.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(proto.getOperation().name()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE -> condition.setValue(proto.getBoolValue() ? 1 : 0);
            case INT_VALUE -> condition.setValue(proto.getIntValue());
            case VALUE_NOT_SET -> throw new IllegalArgumentException("Scenario condition value is not set");
        }

        return condition;
    }

    private DeviceAction mapAction(DeviceActionProto proto) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(proto.getSensorId());
        action.setType(ActionType.valueOf(proto.getType().name()));

        if (proto.hasValue()) {
            action.setValue(proto.getValue());
        }

        return action;
    }

    private Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
