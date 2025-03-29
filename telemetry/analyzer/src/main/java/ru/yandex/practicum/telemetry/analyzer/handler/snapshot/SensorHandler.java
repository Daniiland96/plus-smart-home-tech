package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

public interface SensorHandler {
    String getType();

    Integer handleToValue(SensorStateAvro stateAvro, ConditionType type);
}
