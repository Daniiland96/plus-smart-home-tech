package ru.yandex.practicum.telemetry.analyzer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "collector.kafka.consumer.hub.properties")
    public Properties getKafkaConsumerHubProperties() {
        log.info("{}: Создание Properties для hubConsumer", KafkaClientConfig.class.getSimpleName());
        return new Properties();
    }

    @Bean
    @ConfigurationProperties(prefix = "collector.kafka.consumer.snapshot.properties")
    public Properties getKafkaConsumerSnapshotProperties() {
        log.info("{}: Создание Properties для snapshotConsumer", KafkaClientConfig.class.getSimpleName());
        return new Properties();
    }

    @Bean
    KafkaClient getKafkaClient() {
        return new KafkaClient() {
            private Consumer<String, HubEventAvro> kafkaHubConsumer;
            private Consumer<String, SensorsSnapshotAvro> kafkaSnapshotConsumer;

            @Override
            public Consumer<String, HubEventAvro> getKafkaHubConsumer() {
                log.info("{}: Создание hubConsumer", KafkaClientConfig.class.getSimpleName());
                kafkaHubConsumer = new KafkaConsumer<>(getKafkaConsumerHubProperties());
                return kafkaHubConsumer;
            }

            @Override
            public Consumer<String, SensorsSnapshotAvro> getKafkaSnapshotConsumer() {
                log.info("{}: Создание snapshotConsumer", KafkaClientConfig.class.getSimpleName());
                kafkaSnapshotConsumer = new KafkaConsumer<>(getKafkaConsumerSnapshotProperties());
                return kafkaSnapshotConsumer;
            }

            @Override
            public void close() {
                try {
                    kafkaHubConsumer.commitSync();
                    kafkaSnapshotConsumer.commitSync();
                } finally {
                    log.info("{}: Закрытие hubConsumer", KafkaClientConfig.class.getSimpleName());
                    kafkaHubConsumer.close();
                    log.info("{}: Закрытие snapshotConsumer", KafkaClientConfig.class.getSimpleName());
                    kafkaSnapshotConsumer.close();
                }
            }
        };
    }
}

