package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.order.OrderApi;

@FeignClient(name = "order")
public interface OrderFeignClient extends OrderApi {
}