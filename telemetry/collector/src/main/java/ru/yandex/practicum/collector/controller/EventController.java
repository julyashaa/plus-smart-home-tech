package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.collector.mapper.HubEventAvroMapper;
import ru.yandex.practicum.collector.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.util.AvroSerializer;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {
    private final SensorEventAvroMapper sensorMapper;
    private final HubEventAvroMapper hubMapper;
    private final KafkaEventProducer producer;

    @Value("${collector.kafka.topics.sensors}")
    private String sensorTopic;

    @Value("${collector.kafka.topics.hubs}")
    private String hubTopic;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Received sensor event: {}", event);

        var avro = sensorMapper.mapToAvro(event);

        byte[] bytes = AvroSerializer.serialize(avro, avro.getSchema());

        producer.send(sensorTopic, event.getId(), bytes);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Received hub event: {}", event);

        var avro = hubMapper.mapToAvro(event);

        byte[] bytes = AvroSerializer.serialize(avro, avro.getSchema());

        producer.send(hubTopic, event.getHubId(), bytes);
    }
}
