package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Для бронирования нужно выбрать дату начала")
    private LocalDateTime start;

    @NotNull(message = "Для бронирования нужно выбрать дату конца")
    private LocalDateTime end;

    @NotNull(message = "Нельзя забронировать несуществующий предмет")
    private Long itemId;

    private UserDto booker;

    private BookingStatus status;
}
