package ru.yandex.practicum.collector.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class HubEventAvroMapper {
    private final HubEventMapStructMapper mapStructMapper;

    public HubEventAvro mapToAvro(HubEvent event) {
        HubEventAvro avro = new HubEventAvro();
        avro.setHubId(event.getHubId());
        avro.setTimestamp(toInstant(event.getTimestamp()));

        if (event instanceof DeviceAddedEvent deviceAddedEvent) {
            avro.setPayload(mapStructMapper.toAvro(deviceAddedEvent));

        } else if (event instanceof DeviceRemovedEvent deviceRemovedEvent) {
            avro.setPayload(mapStructMapper.toAvro(deviceRemovedEvent));

        } else if (event instanceof ScenarioAddedEvent scenarioAddedEvent) {
            avro.setPayload(mapStructMapper.toAvro(scenarioAddedEvent));

        } else if (event instanceof ScenarioRemovedEvent scenarioRemovedEvent) {
            avro.setPayload(mapStructMapper.toAvro(scenarioRemovedEvent));

        } else {
            throw new IllegalArgumentException("Unknown hub event type: " + event.getClass().getName());
        }

        return avro;
    }

    private Instant toInstant(Instant timestamp) {
        return timestamp != null ? timestamp : Instant.now();
    }
}
