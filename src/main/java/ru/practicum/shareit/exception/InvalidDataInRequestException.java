package ru.practicum.shareit.exception;

public class InvalidDataInRequestException extends RuntimeException {
    public InvalidDataInRequestException(String message) {
        super(message);
    }
}
