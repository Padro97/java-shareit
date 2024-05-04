package ru.practicum.shareit.exception;

public class ItemNotFoundException extends AbstractNotFoundException {

    private static final String DEFAULT_MESSAGE = "Item by id=%s not found";

    public ItemNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
