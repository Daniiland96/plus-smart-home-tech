package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> hubConsumer;
    private final Map<String, HubEventHandler> hubEventHandlers;
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    @Value("${collector.kafka.topics.hubs-events}")
    private String hubEventsTopic;

    public HubEventProcessor(KafkaClient kafkaClient, List<HubEventHandler> hubEventHandlers) {
        this.hubConsumer = kafkaClient.getKafkaHubConsumer();
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getHubEventType, Function.identity()));
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
        try {
            hubConsumer.subscribe(List.of(hubEventsTopic));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    log.info("{}: Полученное сообщение из kafka: {}", HubEventProcessor.class.getSimpleName(), event);
                    String eventPayloadName = event.getPayload().getClass().getSimpleName();
                    HubEventHandler eventHandler;

                    if (hubEventHandlers.containsKey(eventPayloadName)) {
                        eventHandler = hubEventHandlers.get(eventPayloadName);
                    } else {
                        throw new IllegalArgumentException("Подходящий handler не найден");
                    }
                    log.info("{}: отправка сообщения в handler", HubEventProcessor.class.getSimpleName());
                    eventHandler.handle(event);
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
}
