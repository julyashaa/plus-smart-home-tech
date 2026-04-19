package ru.yandex.practicum.collector.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SensorEventAvroMapper {
    private final SensorEventMapStructMapper mapStructMapper;

    public SensorEventAvro mapToAvro(SensorEvent event) {
        SensorEventAvro avro = new SensorEventAvro();
        avro.setId(event.getId());
        avro.setHubId(event.getHubId());
        avro.setTimestamp(toInstant(event.getTimestamp()));

        if (event instanceof ClimateSensorEvent climateEvent) {
            avro.setPayload(mapStructMapper.toAvro(climateEvent));

        } else if (event instanceof LightSensorEvent lightEvent) {
            avro.setPayload(mapStructMapper.toAvro(lightEvent));

        } else if (event instanceof MotionSensorEvent motionEvent) {
            avro.setPayload(mapStructMapper.toAvro(motionEvent));

        } else if (event instanceof SwitchSensorEvent switchEvent) {
            avro.setPayload(mapStructMapper.toAvro(switchEvent));

        } else if (event instanceof TemperatureSensorEvent temperatureEvent) {
            avro.setPayload(mapStructMapper.toAvro(temperatureEvent));

        } else {
            throw new IllegalArgumentException("Unknown sensor event type: " + event.getClass().getName());
        }

        return avro;
    }

    private Instant toInstant(Instant timestamp) {
        return timestamp != null ? timestamp : Instant.now();
    }
}
