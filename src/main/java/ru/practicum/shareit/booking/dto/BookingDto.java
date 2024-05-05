package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Невозможно забронировать несуществующий предмет")
    private Item item;
    @NotNull(message = "Несуществующий пользователь не может забронировать")
    private UserDto booker;
    @NotNull(message = "Дата старта не должна быть пустой")
    private LocalDate start;
    @NotNull(message = "Дата конца не должна быть пустой")
    private LocalDate end;
    private BookingStatus status;
}
