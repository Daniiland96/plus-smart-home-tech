package ru.yandex.practicum.telemetry.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Configuration
public class KafkaClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "collector.kafka.producer.properties")
    public Properties kafkaProducerProperties() {
        return new Properties();
    }

    @Bean
    Producer<String, SpecificRecordBase> kafkaProducer(Properties kafkaProducerProperties) {
        log.info("Create {}", Producer.class.getSimpleName());
        return new KafkaProducer<>(kafkaProducerProperties);
    }

    @Bean
    KafkaClient getKafkaClient(Producer<String, SpecificRecordBase> kafkaProducer) {
        return new KafkaClient() {

            @Override
            public void send(String topic, Integer partition, Long timestamp, String hubId, SpecificRecordBase event) {
                ProducerRecord<String, SpecificRecordBase> record =
                        new ProducerRecord<>(topic, partition, timestamp, hubId, event);
                log.info("Send in topic {} the record: {}", topic, event);
                Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send(record);

                try {
                    log.info("Record successfully send. Record: {}", recordMetadataFuture.get());
                } catch (ExecutionException | InterruptedException e) {
                    log.info("Executions {} or {}, message: {}", ExecutionException.class.getSimpleName(),
                            InterruptedException.class.getSimpleName(),
                            e.getMessage());
                }
            }

            @Override
            public void close() {
                kafkaProducer.flush();
                log.info("Close {}", Producer.class.getSimpleName());
                kafkaProducer.close();
            }
        };
    }
}
