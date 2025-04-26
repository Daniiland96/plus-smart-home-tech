package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.delivery.DeliveryApi;

@FeignClient(name = "delivery")
public interface DeliveryFeign extends DeliveryApi {
}
