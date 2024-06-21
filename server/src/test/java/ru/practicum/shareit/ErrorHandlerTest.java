package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
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
}
