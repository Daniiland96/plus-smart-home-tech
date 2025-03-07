package ru.yandex.practicum.telemetry.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Slf4j
public abstract class BaseSensorEventMapper<T extends SpecificRecordBase> implements SensorEventMapper {

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public SensorEventAvro mapping(SensorEvent event) {
        if (!event.getType().equals(getSensorEventType())) {
            throw new IllegalArgumentException("Unknown type of event: " + event.getType());
        }

        T payload = mapToAvro(event);

        log.info("Create {}", SensorEventAvro.class.getSimpleName());
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
