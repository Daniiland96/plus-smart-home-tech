package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class ShoppingCartDto {
    @NotBlank
    private String shoppingCartId;

    @NotNull
    private Map<String, Integer> products;
}
