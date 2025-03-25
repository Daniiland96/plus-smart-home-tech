package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.kafka.KafkaClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> hubConsumer;

    @Value("${collector.kafka.topics.hubs-events}")
    private String hubEventsTopic;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public HubEventProcessor(KafkaClient kafkaClient) {
        this.hubConsumer = kafkaClient.getKafkaHubConsumer();
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
        try {
            hubConsumer.subscribe(List.of(hubEventsTopic));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.info("{}: Полученное сообщение из kafka: {}", HubEventProcessor.class.getSimpleName(), record);

                    // Какая то логика

                }
                hubConsumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("{}: Ошибка во время обработки событий от хаба", HubEventProcessor.class.getSimpleName(), e);
        } finally {
            try {
                hubConsumer.commitSync();
            } finally {
                log.info("{}: Закрываем консьюмер", HubEventProcessor.class.getSimpleName());
                hubConsumer.close();
            }
        }
    }
    // ...детали реализации...
}
