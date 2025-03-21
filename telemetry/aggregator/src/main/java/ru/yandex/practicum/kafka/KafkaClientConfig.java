package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Properties;

@Slf4j
@Configuration
public class KafkaClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "collector.kafka.producer.properties")
    public Properties kafkaProducerProperties() {
        log.info("Создание Properties для Producer");
        return new Properties();
    }

    @Bean
    @ConfigurationProperties(prefix = "collector.kafka.consumer.properties")
    public Properties kafkaConsumerProperties() {
        log.info("Создание Properties для Consumer");
        return new Properties();
    }

    @Bean
    KafkaClient getKafkaClient() {
        return new KafkaClient() {
            private Producer<String, SpecificRecordBase> kafkaProducer;
            private Consumer<String, SpecificRecordBase> kafkaConsumer;

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                log.info("Создание Producer");
                kafkaProducer = new KafkaProducer<>(kafkaProducerProperties());
                return kafkaProducer;
            }

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                log.info("Создание Consumer");
                kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProperties());
                return kafkaConsumer;
            }

            @Override
            public void close() {
                try {
                    kafkaProducer.flush();
                    kafkaConsumer.commitSync();
                } finally {
                    log.info("Закрытие {}", Producer.class.getSimpleName());
                    kafkaProducer.close(Duration.ofSeconds(10));
                    log.info("Закрытие {}", Consumer.class.getSimpleName());
                    kafkaConsumer.close();
                }
            }
        };
    }
}
