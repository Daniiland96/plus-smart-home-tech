package ru.yandex.practicum.telemetry.collector.mapper.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceAction;

import java.util.List;

@Component
public class DeviceActionMapper {
    public DeviceActionAvro mapping(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    public List<DeviceActionAvro> mapping(List<DeviceAction> actions) {
        return actions.stream().map(this::mapping).toList();
    }
}
