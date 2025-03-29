package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

@Component
public class TemperatureSensorHandler implements SensorHandler {
    @Override
    public String getType() {
        return TemperatureSensorAvro.class.getTypeName();
    }

    @Override
    public Integer handleToValue(SensorStateAvro stateAvro, ConditionType type) {
        TemperatureSensorAvro sensorAvro = (TemperatureSensorAvro) stateAvro.getData();

        return switch (type) {
            case TEMPERATURE -> sensorAvro.getTemperatureC();
            default -> null;
        };
    }
}
