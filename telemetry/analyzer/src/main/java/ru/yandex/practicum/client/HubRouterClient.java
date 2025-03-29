package ru.yandex.practicum.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HubRouterClient {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterController;

    public void send(Scenario scenario) {
        Map<String, Action> actionMap = scenario.getActions();

        for (String sensorId : actionMap.keySet()) {
            DeviceActionRequest request = mapToDeviceActionRequest(sensorId, actionMap.get(sensorId), scenario);
            hubRouterController.handleDeviceAction(request);
            log.info("{}: Отправляем в hub-router request: {}", HubRouterClient.class.getSimpleName(), request);
        }
    }

    public void send(List<Scenario> scenarios) {
        scenarios.stream().peek(this::send);
    }

    private DeviceActionRequest mapToDeviceActionRequest(String sensorId, Action action, Scenario scenario) {
        log.info("{}: Мапим action в DeviceActionRequest", HubRouterClient.class.getSimpleName());
        DeviceActionProto deviceAction = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(deviceAction)
                .setTimestamp(timestamp)
                .build();

        return request;
    }
}
