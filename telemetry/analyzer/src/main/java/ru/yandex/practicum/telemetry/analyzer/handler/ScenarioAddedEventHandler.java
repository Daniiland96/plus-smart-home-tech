package ru.yandex.practicum.telemetry.analyzer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    public String getHubEventType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        checkForSensors(payload.getConditions(), payload.getActions(), event.getHubId());

        Optional<Scenario> scenarioOpt = scenarioRepository.findByNameAndHubId(payload.getName(), event.getHubId());
        log.info("{}: Удаляем старый scenario, если он есть", ScenarioAddedEventHandler.class.getSimpleName());
        scenarioOpt.ifPresent(oldScenario -> scenarioRepository.deleteByHubIdAndName(
                oldScenario.getHubId()
                , oldScenario.getName()));
        scenarioRepository.flush();

        Scenario scenario = HubEventMapper.mapToScenario(payload, event.getHubId());
        log.info("{}: Сохраняем в БД новый scenario: {}", ScenarioAddedEventHandler.class.getSimpleName(), scenario);
        scenarioRepository.save(scenario);
    }

    private void checkForSensors(List<ScenarioConditionAvro> conditions, List<DeviceActionAvro> actions, String hubId) {
        List<String> conditionSensorIds = conditions.stream().map(ScenarioConditionAvro::getSensorId).toList();
        List<String> actionSensorIds = actions.stream().map(DeviceActionAvro::getSensorId).toList();

        if (!sensorRepository.existsAllByIdInAndHubId(conditionSensorIds, hubId)) {
            throw new IllegalArgumentException("Не найдены устройства, указанные в списке условий");
        }

        if (!sensorRepository.existsAllByIdInAndHubId(actionSensorIds, hubId)) {
            throw new IllegalArgumentException("Не найдены устройства, указанные в списке действий");
        }
    }
}
