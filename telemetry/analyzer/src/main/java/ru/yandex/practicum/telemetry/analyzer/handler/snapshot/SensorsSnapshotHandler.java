package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SensorsSnapshotHandler {

    @GrpcClient("hubrouter")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterController;

    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorHandler> sensorHandlers;

    private SensorsSnapshotHandler(ScenarioRepository scenarioRepository, List<SensorHandler> sensorHandlers) {
        this.scenarioRepository = scenarioRepository;
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorHandler::getType, Function.identity()));
    }

    public void handle(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(snapshot.getHubId());
        if (scenarios.isEmpty()) {
            throw new IllegalArgumentException("У хаба с указанным hubId нет сценариев");
        }
        List<Scenario> validScenarios = scenarios.stream()
                .filter(scenario -> validateScenarioConditions(scenario, snapshot))
                .toList();

    }

    private Boolean validateScenarioConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        Map<String, Condition> conditions = scenario.getConditions();
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
        if (snapshot == null || snapshot.getSensorsState().isEmpty()) {
            return false;
        }
        return conditions.keySet().stream()
                .allMatch(sensorId -> validateScenarioConditions(conditions.get(sensorId), sensorStates.get(sensorId)));
    }

    private Boolean validateScenarioConditions(Condition condition, SensorStateAvro sensorState) {
        if (sensorState == null) {
            return false;
        }

        SensorHandler handler;
        if (sensorHandlers.containsKey(sensorState.getData().getClass().getSimpleName())) {
            handler = sensorHandlers.get(sensorState.getData().getClass().getSimpleName());
        } else {
            throw new IllegalArgumentException("Подходящий handler не найден");
        }

        Integer value = handler.handleToValue(sensorState, condition.getType());
        if (value == null) {
            return false;
        }

        return getConditionOperation(condition, value);
    }

    private Boolean getConditionOperation(Condition condition, Integer value) {
        return switch (condition.getOperation()) {
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
        };
    }
}
