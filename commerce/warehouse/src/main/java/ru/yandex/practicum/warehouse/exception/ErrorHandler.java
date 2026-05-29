package ru.yandex.practicum.warehouse.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(WarehouseBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleWarehouseBadRequest(WarehouseBadRequestException e) {
        log.error("Некорректный запрос", e);
        return e.getMessage();
    }
}