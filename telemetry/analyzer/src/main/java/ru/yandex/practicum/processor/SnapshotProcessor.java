package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.snapshot.SensorsSnapshotHandler;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SnapshotProcessor {
    private final SensorsSnapshotHandler sensorsSnapshotHandler;
    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;

    @Value("${collector.kafka.topics.snapshots-events}")
    private String snapshotEventsTopic;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public SnapshotProcessor(SensorsSnapshotHandler sensorsSnapshotHandler, KafkaClient kafkaClient) {
        this.sensorsSnapshotHandler = sensorsSnapshotHandler;
        this.snapshotConsumer = kafkaClient.getKafkaSnapshotConsumer();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));
        try {
            snapshotConsumer.subscribe(List.of(snapshotEventsTopic));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (!records.isEmpty()) {
                    int count = 0;
                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {

                        log.info("{}: Полученное сообщение из kafka: {}", SnapshotProcessor.class.getSimpleName(), record);
                        sensorsSnapshotHandler.handle(record.value());

                        manageOffsets(record, count, snapshotConsumer);
                        count++;
                    }
                    snapshotConsumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("{}: Ошибка во время обработки snapshot", SnapshotProcessor.class.getSimpleName(), e);
        } finally {
            try {
                snapshotConsumer.commitSync();
            } finally {
                log.info("{}: Закрываем консьюмер", SnapshotProcessor.class.getSimpleName());
                snapshotConsumer.close();
            }
        }
    }

    private static void manageOffsets(
            ConsumerRecord<String, SensorsSnapshotAvro> record,
            int count,
            Consumer<String, SensorsSnapshotAvro> consumer
    ) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}

