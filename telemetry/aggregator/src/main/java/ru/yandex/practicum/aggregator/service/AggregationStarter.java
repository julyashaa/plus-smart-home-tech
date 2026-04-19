package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.util.AvroSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final SnapshotService snapshotService;
    private final SnapshotProducer snapshotProducer;

    @Value("${aggregator.kafka.topics.input}")
    private String inputTopic;

    @Value("${aggregator.kafka.topics.output}")
    private String outputTopic;

    public void start() {
        try {
            log.info("Подписываемся на топик {}", inputTopic);
            consumer.subscribe(Collections.singletonList(inputTopic));

            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    if (event == null) {
                        continue;
                    }

                    Optional<SensorsSnapshotAvro> snapshotOptional = snapshotService.updateState(event);

                    if (snapshotOptional.isPresent()) {
                        SensorsSnapshotAvro snapshot = snapshotOptional.get();

                        byte[] payload = AvroSerializer.serialize(snapshot, snapshot.getSchema());

                        snapshotProducer.send(
                                outputTopic,
                                snapshot.getHubId().toString(),
                                payload
                        );

                        log.info("Отправлен обновлённый снапшот для hubId={}", snapshot.getHubId());
                    }
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {
            log.info("Получен сигнал остановки Aggregator");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.info("Сбрасываем данные продюсера");
                snapshotProducer.flush();

                log.info("Фиксируем смещения консьюмера");
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка при завершении работы Aggregator", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();

                log.info("Закрываем продюсер");
                snapshotProducer.close();
            }
        }
    }
}