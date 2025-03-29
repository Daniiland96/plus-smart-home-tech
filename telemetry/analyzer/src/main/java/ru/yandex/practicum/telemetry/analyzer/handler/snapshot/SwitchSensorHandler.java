package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

@Component
public class SwitchSensorHandler implements SensorHandler {
    @Override
    public String getType() {
        return SwitchSensorAvro.class.getTypeName();
    }

    @Override
    public Integer handleToValue(SensorStateAvro stateAvro, ConditionType type) {
        SwitchSensorAvro sensorAvro = (SwitchSensorAvro) stateAvro.getData();

        return switch (type) {
            case SWITCH -> sensorAvro.getState() ? 1 : 0;
            default -> null;
        };
    }
}
