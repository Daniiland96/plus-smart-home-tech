package ru.yandex.practicum.handler.snapshot;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

public interface SensorHandler {
    String getType();

    Integer handleToValue(SensorStateAvro stateAvro, ConditionType type);
}
