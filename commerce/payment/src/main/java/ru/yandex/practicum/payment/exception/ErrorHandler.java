package ru.yandex.practicum.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoPaymentFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoPaymentFoundException handle(NoPaymentFoundException exception) {
        return exception;
    }
}