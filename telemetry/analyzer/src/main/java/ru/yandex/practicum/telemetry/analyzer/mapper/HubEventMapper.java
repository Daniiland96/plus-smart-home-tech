package ru.yandex.practicum.telemetry.analyzer.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HubEventMapper {

    public static Condition mapToCondition(ScenarioConditionAvro conditionAvro) {
        Condition condition = new Condition();
        condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));
        condition.setValue(getValue(conditionAvro.getValue()));
        log.info("{}: Мапим condition: {}", HubEventMapper.class.getSimpleName(), condition);
        return condition;
    }

    public static List<Condition> mapToCondition(List<ScenarioConditionAvro> conditionsAvro) {
        return conditionsAvro.stream().map(HubEventMapper::mapToCondition).toList();
    }

    public static Action mapToAction(DeviceActionAvro actionAvro) {
        Action action = new Action();
        action.setType(ActionType.valueOf(actionAvro.getType().name()));
        action.setValue(actionAvro.getValue());
        log.info("{}: Мапим action: {}", HubEventMapper.class.getSimpleName(), action);
        return action;
    }

    public static List<Action> mapToAction(List<DeviceActionAvro> actionsAvro) {
        return actionsAvro.stream().map(HubEventMapper::mapToAction).toList();
    }

    public static Sensor mapToSensor(String id, String hubId) {
        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setHubId(hubId);
        log.info("{}: Мапим sensor: {}", HubEventMapper.class.getSimpleName(), sensor);
        return sensor;
    }

    public static Scenario mapToScenario(ScenarioAddedEventAvro scenarioAvro, String hubId) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(scenarioAvro.getName());

        Map<String, Condition> conditionMap = scenarioAvro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, HubEventMapper::mapToCondition));
        Map<String, Action> actionMap = scenarioAvro.getActions().stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, HubEventMapper::mapToAction));
        scenario.setConditions(conditionMap);
        scenario.setActions(actionMap);
        log.info("{}: Мапим scenario: {}", HubEventMapper.class.getSimpleName(), scenario);
        return scenario;
    }

    private static Integer getValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }
}
