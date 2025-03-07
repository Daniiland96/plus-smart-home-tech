package ru.yandex.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaClient;
import ru.yandex.practicum.telemetry.collector.mapper.hub.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.sensor.SensorEventMapper;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {
    @Value("${collector.kafka.producer.topics.sensors-events}")
    private String sensorsEventsTopic;
    @Value("${collector.kafka.producer.topics.hubs-events}")
    private String hubsEventsTopic;

    private final KafkaClient kafkaClient;
    private final Map<SensorEventType, SensorEventMapper> sensorEventMappers;
    private final Map<HubEventType, HubEventMapper> hubEventMappers;

    public CollectorServiceImpl(
            KafkaClient kafkaClient,
            List<SensorEventMapper> sensorEventMapperList,
            List<HubEventMapper> hubEventMapperList
    ) {
        this.kafkaClient = kafkaClient;
        this.sensorEventMappers = sensorEventMapperList.stream()
                .collect(Collectors.toMap(SensorEventMapper::getSensorEventType, Function.identity()));
        this.hubEventMappers = hubEventMapperList.stream()
                .collect(Collectors.toMap(HubEventMapper::getHubEventType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEvent event) {
        SensorEventMapper eventMapper;
        if (sensorEventMappers.containsKey(event.getType())) {
            eventMapper = sensorEventMappers.get(event.getType());
        } else {
            throw new IllegalArgumentException("There is no suitable mapper");
        }

        SensorEventAvro eventAvro = eventMapper.mapping(event);
        log.info("In {} use {}.{} with param: {}",
                CollectorService.class.getSimpleName(),
                KafkaClient.class.getSimpleName(),
                "send()",
                eventAvro);
        kafkaClient.send(
                sensorsEventsTopic,
                null,
                eventAvro.getTimestamp().toEpochMilli(),
                eventAvro.getHubId(),
                eventAvro
        );
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        HubEventMapper eventMapper;
        if (hubEventMappers.containsKey(event.getType())) {
            eventMapper = hubEventMappers.get(event.getType());
        } else {
            throw new IllegalArgumentException("There is no suitable mapper");
        }

        HubEventAvro eventAvro = eventMapper.mapping(event);
        log.info("In {} use {}.{} with param: {}",
                CollectorService.class.getSimpleName(),
                KafkaClient.class.getSimpleName(),
                "send()",
                eventAvro);
        kafkaClient.send(
                hubsEventsTopic,
                null,
                eventAvro.getTimestamp().toEpochMilli(),
                eventAvro.getHubId(),
                eventAvro
        );
    }
}
