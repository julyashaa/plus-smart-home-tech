package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.model.ScenarioActionId;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.model.ScenarioConditionId;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();

        if (payload == null) {
            throw new IllegalArgumentException("Hub event payload is null");
        }

        if (payload instanceof DeviceAddedEventAvro added) {
            handleDeviceAdded(event, added);
        } else if (payload instanceof DeviceRemovedEventAvro removed) {
            handleDeviceRemoved(event, removed);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(event, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(event, scenarioRemoved);
        } else {
            throw new IllegalArgumentException("Unknown hub event payload type: " + payload);
        }
    }

    private void handleDeviceAdded(HubEventAvro event, DeviceAddedEventAvro added) {
        String sensorId = added.getId();
        String hubId = event.getHubId();

        if (sensorRepository.findByIdAndHubId(sensorId, hubId).isEmpty()) {
            Sensor sensor = new Sensor();
            sensor.setId(sensorId);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
    }

    private void handleDeviceRemoved(HubEventAvro event, DeviceRemovedEventAvro removed) {
        String sensorId = removed.getId();
        String hubId = event.getHubId();

        List<ScenarioCondition> scenarioConditions = scenarioConditionRepository.findAllBySensor_Id(sensorId);
        List<Long> conditionIds = scenarioConditions.stream()
                .map(link -> link.getCondition().getId())
                .toList();

        scenarioConditionRepository.deleteAll(scenarioConditions);
        conditionRepository.deleteAllById(conditionIds);

        List<ScenarioAction> scenarioActions = scenarioActionRepository.findAllBySensor_Id(sensorId);
        List<Long> actionIds = scenarioActions.stream()
                .map(link -> link.getAction().getId())
                .toList();

        scenarioActionRepository.deleteAll(scenarioActions);
        actionRepository.deleteAllById(actionIds);

        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .ifPresent(sensorRepository::delete);
    }

    private void handleScenarioAdded(HubEventAvro event, ScenarioAddedEventAvro added) {
        String hubId = event.getHubId();
        String scenarioName = added.getName();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .orElseGet(() -> {
                    Scenario newScenario = new Scenario();
                    newScenario.setHubId(hubId);
                    newScenario.setName(scenarioName);
                    return scenarioRepository.save(newScenario);
                });

        clearScenarioLinks(scenario);

        List<ScenarioCondition> scenarioConditions = new ArrayList<>();
        for (ScenarioConditionAvro conditionAvro : added.getConditions()) {
            String sensorId = conditionAvro.getSensorId();

            Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + sensorId));

            Condition condition = new Condition();
            condition.setType(conditionAvro.getType().name());
            condition.setOperation(conditionAvro.getOperation().name());
            condition.setValue(extractConditionValue(conditionAvro));
            condition = conditionRepository.save(condition);

            ScenarioConditionId id = new ScenarioConditionId();
            id.setScenarioId(scenario.getId());
            id.setSensorId(sensor.getId());
            id.setConditionId(condition.getId());

            ScenarioCondition link = new ScenarioCondition();
            link.setId(id);
            link.setScenario(scenario);
            link.setSensor(sensor);
            link.setCondition(condition);

            scenarioConditions.add(link);
        }
        scenarioConditionRepository.saveAll(scenarioConditions);

        List<ScenarioAction> scenarioActions = new ArrayList<>();
        for (DeviceActionAvro actionAvro : added.getActions()) {
            String sensorId = actionAvro.getSensorId();

            Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + sensorId));

            Action action = new Action();
            action.setType(actionAvro.getType().name());
            action.setValue(actionAvro.getValue());
            action = actionRepository.save(action);

            ScenarioActionId id = new ScenarioActionId();
            id.setScenarioId(scenario.getId());
            id.setSensorId(sensor.getId());
            id.setActionId(action.getId());

            ScenarioAction link = new ScenarioAction();
            link.setId(id);
            link.setScenario(scenario);
            link.setSensor(sensor);
            link.setAction(action);

            scenarioActions.add(link);
        }
        scenarioActionRepository.saveAll(scenarioActions);
    }

    private void handleScenarioRemoved(HubEventAvro event, ScenarioRemovedEventAvro removed) {
        String hubId = event.getHubId();
        String scenarioName = removed.getName();

        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresent(scenario -> {
                    clearScenarioLinks(scenario);
                    scenarioRepository.delete(scenario);
                });
    }

    private void clearScenarioLinks(Scenario scenario) {
        List<Long> conditionIds = scenario.getConditions().stream()
                .map(link -> link.getCondition().getId())
                .toList();

        List<Long> actionIds = scenario.getActions().stream()
                .map(link -> link.getAction().getId())
                .toList();

        scenario.getConditions().clear();
        scenario.getActions().clear();

        conditionRepository.deleteAllById(conditionIds);
        actionRepository.deleteAllById(actionIds);
    }

    private Integer extractConditionValue(ScenarioConditionAvro conditionAvro) {
        Object value = conditionAvro.getValue();

        if (value instanceof Boolean boolValue) {
            return boolValue ? 1 : 0;
        }

        if (value instanceof Integer intValue) {
            return intValue;
        }

        throw new IllegalArgumentException("Unsupported condition value type: " + value);
    }
}