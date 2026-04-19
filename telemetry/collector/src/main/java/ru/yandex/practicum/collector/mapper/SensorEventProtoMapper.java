package ru.yandex.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.ClimateSensorEvent;
import ru.yandex.practicum.collector.dto.sensor.LightSensorEvent;
import ru.yandex.practicum.collector.dto.sensor.MotionSensorEvent;
import ru.yandex.practicum.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.collector.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.collector.dto.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class SensorEventProtoMapper {

    public SensorEvent toDto(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case CLIMATE_SENSOR -> mapClimate(proto);
            case LIGHT_SENSOR -> mapLight(proto);
            case MOTION_SENSOR -> mapMotion(proto);
            case SWITCH_SENSOR -> mapSwitch(proto);
            case TEMPERATURE_SENSOR -> mapTemperature(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload is not set");
        };
    }

    private ClimateSensorEvent mapClimate(SensorEventProto proto) {
        var payload = proto.getClimateSensor();

        ClimateSensorEvent event = new ClimateSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setTemperatureC(payload.getTemperatureC());
        event.setHumidity(payload.getHumidity());
        event.setCo2Level(payload.getCo2Level());
        return event;
    }

    private LightSensorEvent mapLight(SensorEventProto proto) {
        var payload = proto.getLightSensor();

        LightSensorEvent event = new LightSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setLinkQuality(payload.getLinkQuality());
        event.setLuminosity(payload.getLuminosity());
        return event;
    }

    private MotionSensorEvent mapMotion(SensorEventProto proto) {
        var payload = proto.getMotionSensor();

        MotionSensorEvent event = new MotionSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setLinkQuality(payload.getLinkQuality());
        event.setMotion(payload.getMotion());
        event.setVoltage(payload.getVoltage());
        return event;
    }

    private SwitchSensorEvent mapSwitch(SensorEventProto proto) {
        var payload = proto.getSwitchSensor();

        SwitchSensorEvent event = new SwitchSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setState(payload.getState());
        return event;
    }

    private TemperatureSensorEvent mapTemperature(SensorEventProto proto) {
        var payload = proto.getTemperatureSensor();

        TemperatureSensorEvent event = new TemperatureSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        event.setTemperatureC(payload.getTemperatureC());
        event.setTemperatureF(payload.getTemperatureF());
        return event;
    }

    private Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
