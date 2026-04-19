package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnapshotProducer {

    private final KafkaProducer<String, byte[]> producer;

    public void send(String topic, String key, byte[] data) {
        producer.send(new ProducerRecord<>(topic, key, data));
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }
}
