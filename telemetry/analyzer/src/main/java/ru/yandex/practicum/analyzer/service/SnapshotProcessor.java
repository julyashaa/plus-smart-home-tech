package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioEvaluationService scenarioEvaluationService;

    @Value("${analyzer.kafka.topics.snapshots}")
    private String snapshotTopic;

    public void start() {
        try {
            log.info("Подписываемся на топик {}", snapshotTopic);
            snapshotConsumer.subscribe(Collections.singletonList(snapshotTopic));

            while (true) {
                var records = snapshotConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    try {
                        SensorsSnapshotAvro snapshot = record.value();

                        if (snapshot == null) {
                            continue;
                        }

                        String hubId = snapshot.getHubId();
                        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

                        if (!scenarios.isEmpty()) {
                            scenarioEvaluationService.evaluate(snapshot, scenarios);
                        }
                    } catch (Exception e) {
                        log.error("Ошибка обработки snapshot: topic={}, partition={}, offset={}",
                                record.topic(), record.partition(), record.offset(), e);
                    }
                }

                if (!records.isEmpty()) {
                    snapshotConsumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("Получен сигнал остановки SnapshotProcessor");
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            try {
                snapshotConsumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка при фиксации смещений SnapshotProcessor", e);
            } finally {
                snapshotConsumer.close();
            }
        }
    }
}