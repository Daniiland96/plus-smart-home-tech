package ru.yandex.practicum.telemetry.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.concurrent.ExecutionException;

public interface KafkaClient {
    void send(String topic, Integer partition, Long timestamp, String hubId, SpecificRecordBase event);

    void close();
}
