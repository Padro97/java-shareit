package ru.practicum.shareit.exception;

public class DuplicatedEmailException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Operation is not permitted due to duplicated email";

    public DuplicatedEmailException() {
        super(DEFAULT_MESSAGE);
    }
}
