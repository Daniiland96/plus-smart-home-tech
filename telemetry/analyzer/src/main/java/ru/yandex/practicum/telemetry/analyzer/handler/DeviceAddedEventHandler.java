package ru.yandex.practicum.telemetry.analyzer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getHubEventType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();
        log.info("{}: Достаем из event, payload: {}", DeviceAddedEventHandler.class.getSimpleName(), payload);
        Sensor sensor = new Sensor();
        sensor.setId(payload.getId());
        sensor.setHubId(event.getHubId());
        log.info("{}: Сохраняем в БД sensor: {}", DeviceAddedEventHandler.class.getSimpleName(), sensor);
        sensorRepository.save(sensor);
    }
}
