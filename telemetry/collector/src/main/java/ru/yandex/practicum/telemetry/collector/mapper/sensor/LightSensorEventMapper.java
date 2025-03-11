package ru.yandex.practicum.telemetry.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Slf4j
@Component
public class LightSensorEventMapper extends BaseSensorEventMapper<LightSensorAvro> {
    @Override
    protected LightSensorAvro mapToAvroPayload(SensorEvent event) {
        LightSensorEvent sensorEvent = (LightSensorEvent) event;
        log.info("Mapper bring event to {}, result: {}", LightSensorEvent.class.getSimpleName(), sensorEvent);
        return LightSensorAvro.newBuilder()
                .setLinkQuality(sensorEvent.getLinkQuality())
                .setLuminosityl(sensorEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
