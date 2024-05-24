package ru.practicum.shareit.user;

public class UserHasAlreadyCreatedException extends RuntimeException {
    public UserHasAlreadyCreatedException(String msg) {
        super(msg);
    }
}
