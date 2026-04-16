package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Component
public class SensorEventAvroMapper {
    public SensorEventAvro mapToAvro(SensorEvent event) {
        SensorEventAvro avro = new SensorEventAvro();
        avro.setId(event.getId());
        avro.setHubId(event.getHubId());
        avro.setTimestamp(toInstant(event.getTimestamp()));

        if (event instanceof ClimateSensorEvent climateEvent) {
            ClimateSensorAvro payload = new ClimateSensorAvro();
            payload.setTemperatureC(getOrDefault(climateEvent.getTemperatureC()));
            payload.setHumidity(getOrDefault(climateEvent.getHumidity()));
            payload.setCo2Level(getOrDefault(climateEvent.getCo2Level()));
            avro.setPayload(payload);
        } else if (event instanceof LightSensorEvent lightEvent) {
            LightSensorAvro payload = new LightSensorAvro();
            payload.setLinkQuality(getOrDefault(lightEvent.getLinkQuality()));
            payload.setLuminosity(getOrDefault(lightEvent.getLuminosity()));
            avro.setPayload(payload);
        } else if (event instanceof MotionSensorEvent motionEvent) {
            MotionSensorAvro payload = new MotionSensorAvro();
            payload.setLinkQuality(getOrDefault(motionEvent.getLinkQuality()));
            payload.setMotion(Boolean.TRUE.equals(motionEvent.getMotion()));
            payload.setVoltage(getOrDefault(motionEvent.getVoltage()));
            avro.setPayload(payload);
        } else if (event instanceof SwitchSensorEvent switchEvent) {
            SwitchSensorAvro payload = new SwitchSensorAvro();
            payload.setState(Boolean.TRUE.equals(switchEvent.getState()));
            avro.setPayload(payload);
        } else if (event instanceof TemperatureSensorEvent temperatureEvent) {
            TemperatureSensorAvro payload = new TemperatureSensorAvro();
            payload.setId(temperatureEvent.getId());
            payload.setHubId(temperatureEvent.getHubId());
            payload.setTimestamp(toInstant(temperatureEvent.getTimestamp()));
            payload.setTemperatureC(getOrDefault(temperatureEvent.getTemperatureC()));
            payload.setTemperatureF(getOrDefault(temperatureEvent.getTemperatureF()));
            avro.setPayload(payload);
        } else {
            throw new IllegalArgumentException("Unknown sensor event type: " + event.getClass().getName());
        }

        return avro;
    }

    private Instant toInstant(Instant timestamp) {
        return timestamp != null ? timestamp : Instant.now();
    }

    private int getOrDefault(Integer value) {
        return value != null ? value : 0;
    }
}
