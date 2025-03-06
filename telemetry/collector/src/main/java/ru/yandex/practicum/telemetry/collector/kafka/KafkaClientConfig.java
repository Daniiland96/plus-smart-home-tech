package ru.yandex.practicum.telemetry.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Configuration
public class KafkaClientConfig {
    @Value("${collector.kafka.producer.properties.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${collector.kafka.producer.properties.key-serializer}")
    private String keySerializer;
    @Value("${collector.kafka.producer.properties.value-serializer}")
    private String valueSerializer;

    @Bean
    Producer<String, SpecificRecordBase> getProducer() {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        log.info("Create {}", Producer.class.getSimpleName());
        return new KafkaProducer<>(configs);
    }

    @Bean
    KafkaClient getKafkaClient() {
        return new KafkaClient() {
            private final Producer<String, SpecificRecordBase> producer = getProducer();

            @Override
            public void send(String topic, Integer partition, Long timestamp, String hubId, SpecificRecordBase event) {
                ProducerRecord<String, SpecificRecordBase> record =
                        new ProducerRecord<>(topic, partition, timestamp, hubId, event);
                log.info("Send in topic {} the record: {}", topic, event);
                Future<RecordMetadata> recordMetadataFuture = producer.send(record);

                // Удалить ->
                try {
                    log.info("Record successfully send. Record: {}", recordMetadataFuture.get());
                } catch (ExecutionException | InterruptedException e) {
                    log.info("Executions {} or {}, message: {}", ExecutionException.class.getSimpleName(),
                            InterruptedException.class.getSimpleName(),
                            e.getMessage());
                }
                // <-
            }

            @Override
            public void close() {
                producer.flush();
                log.info("Close {}", Producer.class.getSimpleName());
                producer.close();
            }
        };
    }
}
