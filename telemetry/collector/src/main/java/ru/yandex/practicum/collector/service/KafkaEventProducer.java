package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void send(String topic, String key, byte[] data) {
        kafkaTemplate.send(topic, key, data);
    }
}
