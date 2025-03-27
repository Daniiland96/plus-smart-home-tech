package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;

@Slf4j
@Service
public class GrpcClient {
    @net.devh.boot.grpc.client.inject.GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    //    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void collectSensorEvent() {
        SensorEventProto event = SensorEventProto.newBuilder()
                .setId("MySensorEventId." + (Math.random() * 10))
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .setHubId("MyHubId." + (Math.random() * 10))
                .setLightSensorEvent(
                        LightSensorEventProto.newBuilder()
                                .setLinkQuality(22)
                                .setLuminosity(33)
                                .build()
                )
                .build();

//        collectorStub.collectSensorEvent(event);
    }

    public void collectHubEvent() {
//        HubEventProto addEvent = HubEventProto.newBuilder()
//                .setHubId("MyHubId." + (Math.random() * 10))
//                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
//                .setDeviceAdded(
//                        DeviceAddedEventProto.newBuilder()
//                                .setId("MySensorEventId." + (Math.random() * 10))
//                                .setType(DeviceTypeProto.LIGHT_SENSOR)
//                                .build()
//                )
//                .build();

        HubEventProto addEvent = HubEventProto.newBuilder()
                .setHubId("MyHubId.")
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .setDeviceAdded(
                        DeviceAddedEventProto.newBuilder()
                                .setId("MySensorEventId.")
                                .setType(DeviceTypeProto.LIGHT_SENSOR)
                                .build()
                )
                .build();

        HubEventProto deleteEvent = HubEventProto.newBuilder()
                .setHubId(addEvent.getHubId())
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .setDeviceRemoved(
                        DeviceRemovedEventProto.newBuilder()
                                .setId(addEvent.getDeviceAdded().getId())
                                .build()
                )
                .build();
//        collectorStub.collectHubEvent(addEvent);
        collectorStub.collectHubEvent(deleteEvent);
    }
}
