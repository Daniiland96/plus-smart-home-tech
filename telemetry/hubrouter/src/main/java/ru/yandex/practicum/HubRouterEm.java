package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
public class HubRouterEm {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(HubRouterEm.class, args);
        GrpcClient client = context.getBean(GrpcClient.class);
        client.collectSensorEvent();
        client.collectHubEvent();
    }
}
