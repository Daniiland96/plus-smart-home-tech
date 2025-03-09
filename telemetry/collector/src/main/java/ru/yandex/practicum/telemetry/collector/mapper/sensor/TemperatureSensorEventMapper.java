package ru.yandex.practicum.telemetry.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.TemperatureSensorEvent;

@Slf4j
@Component
public class TemperatureSensorEventMapper extends BaseSensorEventMapper<TemperatureSensorAvro> {
    @Override
    protected TemperatureSensorAvro mapToAvroPayload(SensorEvent event) {
        TemperatureSensorEvent sensorEvent = (TemperatureSensorEvent) event;
        log.info("Mapper bring event to {}, result: {}", TemperatureSensorEvent.class.getSimpleName(), sensorEvent);
        return TemperatureSensorAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setTemperatureC(sensorEvent.getTemperatureC())
                .setTemperatureF(sensorEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
