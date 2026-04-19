package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.collector.mapper.HubEventAvroMapper;
import ru.yandex.practicum.collector.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.collector.util.AvroSerializer;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final SensorEventAvroMapper sensorMapper;
    private final HubEventAvroMapper hubMapper;
    private final KafkaEventProducer producer;

    @Value("${collector.kafka.topics.sensors}")
    private String sensorTopic;

    @Value("${collector.kafka.topics.hubs}")
    private String hubTopic;

    public void processSensorEvent(SensorEvent event) {
        log.info("Received sensor event: {}", event);

        var avro = sensorMapper.mapToAvro(event);
        byte[] bytes = AvroSerializer.serialize(avro, avro.getSchema());

        producer.send(sensorTopic, event.getId(), bytes);
    }

    public void processHubEvent(HubEvent event) {
        log.info("Received hub event: {}", event);

        var avro = hubMapper.mapToAvro(event);
        byte[] bytes = AvroSerializer.serialize(avro, avro.getSchema());

        producer.send(hubTopic, event.getHubId(), bytes);
    }
}