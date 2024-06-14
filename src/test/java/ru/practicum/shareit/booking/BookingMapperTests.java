package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoApproved;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTests {

    private BookingMapper bookingMapper;

    @BeforeEach
    public void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    public void testFromDto() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking = bookingMapper.fromDto(bookingDto);

        assertNull(booking.getItem(), "Item should be null");
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    public void testToAnswerDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        BookingDtoForAnswer bookingDtoForAnswer = bookingMapper.toAnswerDto(booking);

        assertEquals(booking.getId(), bookingDtoForAnswer.getId());
        assertEquals(booking.getStart(), bookingDtoForAnswer.getStart());
        assertEquals(booking.getEnd(), bookingDtoForAnswer.getEnd());
        assertEquals(booking.getStatus(), bookingDtoForAnswer.getStatus());
    }

    @Test
    public void testToListAnswerDto() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build();

        List<BookingDtoForAnswer> bookingDtoForAnswerList = bookingMapper.toListAnswerDto(List.of(booking1, booking2));

        assertThat(bookingDtoForAnswerList).hasSize(2);
        assertEquals(booking1.getId(), bookingDtoForAnswerList.get(0).getId());
        assertEquals(booking2.getId(), bookingDtoForAnswerList.get(1).getId());
    }

    @Test
    public void testToApprovedDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        BookingDtoApproved bookingDtoApproved = bookingMapper.toApprovedDto(booking);

        assertEquals(booking.getId(), bookingDtoApproved.getId());
        assertEquals(booking.getStatus(), bookingDtoApproved.getStatus());
    }

    @Test
    public void testToItemWithBookings() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        BookingDtoForBookingItems bookingDtoForBookingItems = bookingMapper.toItemWithBookings(booking);

        assertEquals(booking.getId(), bookingDtoForBookingItems.getId());
        assertEquals(booking.getStart(), bookingDtoForBookingItems.getStart());
        assertEquals(booking.getEnd(), bookingDtoForBookingItems.getEnd());
        assertEquals(booking.getStatus(), bookingDtoForBookingItems.getStatus());
    }
}
