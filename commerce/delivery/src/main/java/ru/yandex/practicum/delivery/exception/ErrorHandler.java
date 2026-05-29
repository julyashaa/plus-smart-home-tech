package ru.yandex.practicum.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoDeliveryFoundException handleNoOrderFound(NoDeliveryFoundException exception) {
        return exception;
    }

}
