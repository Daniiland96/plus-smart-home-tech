package ru.yandex.practicum.dto.shoppingCart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangeProductQuantityRequest {
    @NotBlank
    private String productId;
    @Min(1)
    @NotNull
    private Integer newQuantity;
}
