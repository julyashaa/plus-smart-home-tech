package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScenarioEvaluationService {

    private final ActionExecutor actionExecutor;
    private final ScenarioRepository scenarioRepository;

    @Transactional(readOnly = true)
    public void evaluate(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId().toString();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : scenarios) {
            boolean allConditionsMatch = scenario.getConditions().stream()
                    .allMatch(link -> matchesCondition(snapshot, link));

            if (allConditionsMatch) {
                log.info("Scenario matched: hubId={}, scenario={}",
                        scenario.getHubId(), scenario.getName());
                executeActions(scenario);
            }
        }
    }

    private boolean matchesCondition(SensorsSnapshotAvro snapshot, ScenarioCondition link) {
        String sensorId = link.getSensor().getId();
        Condition condition = link.getCondition();

        SensorStateAvro state = snapshot.getSensorsState().entrySet().stream()
                .filter(entry -> entry.getKey().toString().equals(sensorId))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (state == null) {
            return false;
        }

        Integer actualValue = extractSensorValue(state, condition.getType());
        if (actualValue == null) {
            return false;
        }

        int expected = condition.getValue();

        return switch (condition.getOperation()) {
            case "EQUALS" -> actualValue.equals(expected);
            case "GREATER_THAN" -> actualValue > expected;
            case "LOWER_THAN" -> actualValue < expected;
            default -> throw new IllegalArgumentException("Unknown operation: " + condition.getOperation());
        };
    }

    private Integer extractSensorValue(SensorStateAvro state, String conditionType) {
        Object data = state.getData();

        return switch (conditionType) {
            case "MOTION" -> {
                if (data instanceof MotionSensorAvro motion) {
                    yield motion.getMotion() ? 1 : 0;
                }
                yield null;
            }
            case "LUMINOSITY" -> {
                if (data instanceof LightSensorAvro light) {
                    yield light.getLuminosity();
                }
                yield null;
            }
            case "SWITCH" -> {
                if (data instanceof SwitchSensorAvro sw) {
                    yield sw.getState() ? 1 : 0;
                }
                yield null;
            }
            case "TEMPERATURE" -> {
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getTemperatureC();
                }
                if (data instanceof TemperatureSensorAvro temperature) {
                    yield temperature.getTemperatureC();
                }
                yield null;
            }
            case "CO2LEVEL" -> {
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getCo2Level();
                }
                yield null;
            }
            case "HUMIDITY" -> {
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getHumidity();
                }
                yield null;
            }
            default -> throw new IllegalArgumentException("Unknown condition type: " + conditionType);
        };
    }

    private void executeActions(Scenario scenario) {
        for (ScenarioAction link : scenario.getActions()) {
            Action action = link.getAction();
            String sensorId = link.getSensor().getId();

            actionExecutor.execute(
                    scenario.getHubId(),
                    scenario.getName(),
                    sensorId,
                    action.getType(),
                    action.getValue()
            );
        }
    }
}