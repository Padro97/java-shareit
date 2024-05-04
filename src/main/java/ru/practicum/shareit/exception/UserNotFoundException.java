package ru.practicum.shareit.exception;

public class UserNotFoundException extends AbstractNotFoundException {

    private static final String DEFAULT_MESSAGE = "User by id=%s not found";

    public UserNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
