package ru.yandex.practicum.telemetry.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SwitchSensorEvent;

@Slf4j
@Component
public class SwitchSensorEventMapper extends BaseSensorEventMapper<SwitchSensorAvro> {
    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        SwitchSensorEvent sensorEvent = (SwitchSensorEvent) event;
        log.info("Mapper bring event to {}, result: {}", SwitchSensorEvent.class.getSimpleName(), sensorEvent);
        return SwitchSensorAvro.newBuilder()
                .setState(sensorEvent.getState())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
