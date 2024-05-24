package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface BookingService {
    BookingDtoForAnswer add(BookingDto booking, Long userId) throws AccessDeniedException;

    BookingDtoForAnswer approve(Long id, Boolean isApproved, Long userId) throws AccessDeniedException;

    BookingDtoForAnswer getById(Long id, Long userId) throws AccessDeniedException;

    List<BookingDtoForAnswer> getByUser(String state, Long userId);

    List<BookingDtoForAnswer> getByOwner(String state, Long userId);
}
