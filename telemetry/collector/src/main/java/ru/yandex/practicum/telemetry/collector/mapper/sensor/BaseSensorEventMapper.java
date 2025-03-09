package ru.yandex.practicum.telemetry.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

@Slf4j
public abstract class BaseSensorEventMapper<T extends SpecificRecordBase> implements SensorEventMapper {

    protected abstract T mapToAvroPayload(SensorEvent event);

    @Override
    public SensorEventAvro mapToAvro(SensorEvent event) {
        if (!event.getType().equals(getSensorEventType())) {
            throw new IllegalArgumentException("Unknown type of event: " + event.getType());
        }

        T payload = mapToAvroPayload(event);

        log.info("Create {}", SensorEventAvro.class.getSimpleName());
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
