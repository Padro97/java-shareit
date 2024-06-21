package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler;
    private WebRequest request;

    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
        request = mock(WebRequest.class);
    }

    @Test
    public void testHandleValidationException() {
        ValidationException exception = new ValidationException("Validation failed");
        var response = errorHandler.handleValidationException(exception);

        assertEquals("Validation failed", response.get("Ошибка валидации"));
    }

    @Test
    public void testHandleAccessDenied() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        var response = errorHandler.handleAccessDenied(exception);

        assertEquals("Access denied", response.get("Ошибка доступа"));
    }

    @Test
    public void testHandleNoSuchElement() {
        NoSuchElementException exception = new NoSuchElementException("No such element");
        var response = errorHandler.handleNoSuchElement(exception);

        assertEquals("No such element", response.get("Ошибка поиска элемента"));
    }

    @Test
    public void testHandleEntityNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");
        var response = errorHandler.handleEntityNotFound(exception);

        assertEquals("Entity not found", response.get("Ошибка поиска элемента"));
    }

    @Test
    public void testHandleIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");
        var response = errorHandler.handleIllegalArgument(exception);

        assertEquals("Illegal argument", response.get("error"));
    }

    @Test
    public void testHandleIllegalState() {
        IllegalStateException exception = new IllegalStateException("Illegal state");
        var response = errorHandler.handleIllegalState(exception);

        assertEquals("Illegal state", response.get("error"));
    }
}
