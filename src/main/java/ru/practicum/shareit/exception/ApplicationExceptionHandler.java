package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final InvalidDataInRequestException exception) {
        logger.error("Invalid data in request: {}", exception.getMessage());
        return new ErrorResponse("Invalid data in request.", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final AbstractNotFoundException exception) {
        logger.error("Entity not found: {}", exception.getMessage());
        return new ErrorResponse("Entity not found", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException exception) {
        logger.error("Access denied: {}", exception.getMessage());
        return new ErrorResponse("Access denied", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownException(final RuntimeException exception) {
        logger.error("Unexpected error: {}", exception.getMessage());
        return new ErrorResponse("Unexpected error", exception.getMessage());
    }
}
