package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.payment.PaymentApi;

@FeignClient(name = "payment")
public interface PaymentFeign extends PaymentApi {
}
