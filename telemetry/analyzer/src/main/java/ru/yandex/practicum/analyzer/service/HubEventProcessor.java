package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubEventConsumer;
    private final HubEventService hubEventService;

    @Value("${analyzer.kafka.topics.hubs}")
    private String hubTopic;

    @Override
    public void run() {
        try {
            log.info("Подписываемся на топик {}", hubTopic);
            hubEventConsumer.subscribe(Collections.singletonList(hubTopic));

            while (true) {
                var records = hubEventConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        HubEventAvro event = record.value();
                        if (event == null) {
                            continue;
                        }
                        hubEventService.handle(event);
                    } catch (Exception e) {
                        log.error("Ошибка обработки hub event: topic={}, partition={}, offset={}",
                                record.topic(), record.partition(), record.offset(), e);
                    }
                }

                if (!records.isEmpty()) {
                    hubEventConsumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("Получен сигнал остановки HubEventProcessor");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий хаба", e);
        } finally {
            try {
                hubEventConsumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка при фиксации смещений HubEventProcessor", e);
            } finally {
                hubEventConsumer.close();
            }
        }
    }
}