package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionType;

@Component
public class ClimateSensorHandler implements SensorHandler {
    @Override
    public String getType() {
        return ClimateSensorAvro.class.getTypeName();
    }

    @Override
    public Integer handleToValue(SensorStateAvro stateAvro, ConditionType type) {
        ClimateSensorAvro sensorAvro = (ClimateSensorAvro) stateAvro.getData();

        return switch (type) {
            case TEMPERATURE -> sensorAvro.getTemperatureC();
            case HUMIDITY -> sensorAvro.getHumidity();
            case CO2LEVEL -> sensorAvro.getCo2Level();
            default -> null;
        };
    }
}
