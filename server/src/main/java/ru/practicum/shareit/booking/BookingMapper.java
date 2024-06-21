package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoApproved;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;

import java.util.List;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "item", ignore = true)
    Booking fromDto(BookingDto dto);

    BookingDtoForAnswer toAnswerDto(Booking booking);

    List<BookingDtoForAnswer> toListAnswerDto(List<Booking> bookings);

    BookingDtoApproved toApprovedDto(Booking booking);

    BookingDtoForBookingItems toItemWithBookings(Booking booking);
}
