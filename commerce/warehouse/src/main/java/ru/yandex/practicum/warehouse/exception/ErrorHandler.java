package ru.yandex.practicum.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(WarehouseBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleWarehouseBadRequest(WarehouseBadRequestException e) {
        return e.getMessage();
    }
}