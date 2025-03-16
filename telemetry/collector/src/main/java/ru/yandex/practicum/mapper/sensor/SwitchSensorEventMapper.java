package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Slf4j
@Component
public class SwitchSensorEventMapper extends BaseSensorEventMapper<SwitchSensorAvro> {
    @Override
    protected SwitchSensorAvro mapToAvroPayload(SensorEventProto event) {
        SwitchSensorEventProto sensorEvent = event.getSwitchSensorEvent();
        log.info("Mapper bring event to {}, result: {}", SwitchSensorEventProto.class.getSimpleName(), sensorEvent);
        return SwitchSensorAvro.newBuilder()
                .setState(sensorEvent.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getSensorEventType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}
