package ru.yandex.practicum.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoOrderFoundException handleNoOrderFound(NoOrderFoundException exception) {
        return exception;
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotAuthorizedUserException handleNotAuthorized(NotAuthorizedUserException exception) {
        return exception;
    }
}