package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.kafka.KafkaClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class SnapshotProcessor {
    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;

    @Value("${collector.kafka.topics.snapshots-events}")
    private String snapshotEventsTopic;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public SnapshotProcessor(KafkaClient kafkaClient) {
        this.snapshotConsumer = kafkaClient.getKafkaSnapshotConsumer();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));
        try {
            snapshotConsumer.subscribe(List.of(snapshotEventsTopic));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    log.info("{}: Полученное сообщение из kafka: {}", SnapshotProcessor.class.getSimpleName(), record);

                    // Какая то логика

                }
                snapshotConsumer.commitAsync();
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
    // ...детали реализации...
}

