package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exeption.ProductNotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    public record ErrorResponse (String message) {
    }

    sfesefzsef
}