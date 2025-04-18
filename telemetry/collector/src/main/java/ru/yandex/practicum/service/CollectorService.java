package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface CollectorService {
    void collectSensorEvent(SensorEventProto event);

    void collectHubEvent(HubEventProto event);
}
