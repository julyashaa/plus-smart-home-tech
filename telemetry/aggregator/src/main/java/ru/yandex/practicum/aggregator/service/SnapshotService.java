package ru.yandex.practicum.aggregator.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        String hubId = event.getHubId().toString();
        String sensorId = event.getId().toString();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                hubId,
                id -> {
                    SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
                    newSnapshot.setHubId(event.getHubId());
                    newSnapshot.setTimestamp(event.getTimestamp());
                    newSnapshot.setSensorsState(new HashMap<>());
                    return newSnapshot;
                }
        );

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

        if (oldState != null) {
            boolean eventIsOlder = event.getTimestamp().isBefore(oldState.getTimestamp());
            boolean dataIsSame = oldState.getData().equals(event.getPayload());

            if (eventIsOlder || dataIsSame) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }
}
