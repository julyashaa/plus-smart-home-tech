package ru.yandex.practicum.collector.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.collector.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SensorEventMapStructMapper {
    ClimateSensorAvro toAvro(ClimateSensorEvent event);

    LightSensorAvro toAvro(LightSensorEvent event);

    MotionSensorAvro toAvro(MotionSensorEvent event);

    SwitchSensorAvro toAvro(SwitchSensorEvent event);

    TemperatureSensorAvro toAvro(TemperatureSensorEvent event);

    default int map(Integer value) {
        return value == null ? 0 : value;
    }

    default boolean map(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    default Instant map(Instant value) {
        return value == null ? Instant.now() : value;
    }
}
