package ru.practicum.shareit;

import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;

public class Checks {
    public static void pageParams(int from, int size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("from и size не могут быть меньше 0");
        }
    }

    public static void dateTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new ValidationException("Неправильное время");
        }
    }
}
