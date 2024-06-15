package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {
    private Long id;

    private String name;

    private String description;

    private UserDto owner;

    private Boolean available;

    private ItemRequestDto request;

    private BookingDtoForBookingItems nextBooking;

    private BookingDtoForBookingItems lastBooking;

    private List<CommentDto> comments;
}
