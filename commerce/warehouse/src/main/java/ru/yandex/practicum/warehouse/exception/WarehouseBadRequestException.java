package ru.yandex.practicum.warehouse.exception;

public class WarehouseBadRequestException extends RuntimeException {
    public WarehouseBadRequestException(String message) {
        super(message);
    }
}