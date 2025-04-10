package ru.yandex.practicum.exeption;

public class NotFoundShoppingCartException extends RuntimeException {
    public NotFoundShoppingCartException(String message) {
        super(message);
    }
}
