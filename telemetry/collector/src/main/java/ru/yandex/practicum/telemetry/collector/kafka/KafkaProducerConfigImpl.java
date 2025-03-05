package ru.yandex.practicum.telemetry.collector.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Component
public class KafkaProducerConfigImpl implements KafkaProducerConfig {

    @Override
    public Properties getProperties() {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
        configs.put(ProducerConfig.LINGER_MS_CONFIG, 3000);
        return configs;
    }
}
