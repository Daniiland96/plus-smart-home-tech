package ru.yandex.practicum.telemetry.analyzer.handler.hub;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getHubEventType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();
        log.info("{}: Достаем из event, payload: {}", DeviceAddedEventHandler.class.getSimpleName(), payload);

        log.info("{}: Удаляем из БД sensor по hubId: {} и sensorId: {}",
                DeviceRemovedEventAvro.class.getSimpleName(), event.getHubId(), payload.getId());
        sensorRepository.deleteByIdAndHubId(payload.getId(), event.getHubId());
    }
}
