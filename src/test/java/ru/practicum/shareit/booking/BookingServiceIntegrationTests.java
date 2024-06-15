package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class BookingServiceIntegrationTests {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    @Rollback
    public void setUp() {
        owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        owner = userRepository.save(owner);

        booker = User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();
        booker = userRepository.save(booker);

        item = Item.builder()
                .name("Test Item")
                .description("Description of test item")
                .owner(owner)
                .available(true)
                .build();
        item = itemRepository.save(item);

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        booking = bookingRepository.save(booking);
    }

    @Test
    public void testApproveBooking() throws Exception {
        Long bookingId = booking.getId();
        Long ownerId = owner.getId();

        BookingDtoForAnswer approvedBooking = bookingService.approve(bookingId, true, ownerId);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(approvedBooking.getId()).isEqualTo(bookingId);
    }

    @Test
    public void testRejectBooking() throws Exception {
        Long bookingId = booking.getId();
        Long ownerId = owner.getId();

        BookingDtoForAnswer rejectedBooking = bookingService.approve(bookingId, false, ownerId);

        assertThat(rejectedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(rejectedBooking.getId()).isEqualTo(bookingId);
    }

    @Test
    public void testApproveBookingByNonOwnerThrowsException() {
        Long bookingId = booking.getId();
        Long bookerId = booker.getId();

        assertThrows(AccessDeniedException.class, () -> {
            bookingService.approve(bookingId, true, bookerId);
        });
    }

    @Test
    public void testApproveAlreadyApprovedBookingThrowsException() throws Exception {
        Long bookingId = booking.getId();
        Long ownerId = owner.getId();

        bookingService.approve(bookingId, true, ownerId);

        assertThrows(ValidationException.class, () -> {
            bookingService.approve(bookingId, true, ownerId);
        });
    }

    @Test
    public void testApproveNonExistentBookingThrowsException() {
        Long nonExistentBookingId = 999L;
        Long ownerId = owner.getId();

        assertThrows(NoSuchElementException.class, () -> {
            bookingService.approve(nonExistentBookingId, true, ownerId);
        });
    }

    @Test
    public void testGetByOwnerAllState() {
        Long ownerId = owner.getId();
        List<BookingDtoForAnswer> bookings = bookingService.getByOwner(0L, 10L, "ALL", ownerId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByOwnerCurrentState() {
        Long ownerId = owner.getId();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking = bookingRepository.save(booking);

        List<BookingDtoForAnswer> bookings = bookingService.getByOwner(0L, 10L, "CURRENT", ownerId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByOwnerPastState() {
        Long ownerId = owner.getId();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking = bookingRepository.save(booking);

        List<BookingDtoForAnswer> bookings = bookingService.getByOwner(0L, 10L, "PAST", ownerId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByOwnerFutureState() {
        Long ownerId = owner.getId();
        List<BookingDtoForAnswer> bookings = bookingService.getByOwner(0L, 10L, "FUTURE", ownerId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByOwnerWaitingState() {
        Long ownerId = owner.getId();
        List<BookingDtoForAnswer> bookings = bookingService.getByOwner(0L, 10L, "WAITING", ownerId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByOwnerRejectedState() {
        Long ownerId = owner.getId();
        booking.setStatus(BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);

        List<BookingDtoForAnswer> bookings = bookingService.getByOwner(0L, 10L, "REJECTED", ownerId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByOwnerThrowsExceptionForInvalidPagination() {
        Long ownerId = owner.getId();

        assertThatThrownBy(() -> bookingService.getByOwner(-1L, 10L, "ALL", ownerId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");

        assertThatThrownBy(() -> bookingService.getByOwner(0L, -1L, "ALL", ownerId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");
    }

    @Test
    public void testGetByOwnerThrowsExceptionForUnknownState() {
        Long ownerId = owner.getId();
        assertThatThrownBy(() -> bookingService.getByOwner(0L, 10L, "UNKNOWN", ownerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown state: UNKNOWN");
    }

    @Test
    public void testGetByUserAllState() {
        Long userId = booker.getId();
        List<BookingDtoForAnswer> bookings = bookingService.getByUser(0L, 10L, "ALL", userId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByUserCurrentState() {
        Long userId = booker.getId();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking = bookingRepository.save(booking);

        List<BookingDtoForAnswer> bookings = bookingService.getByUser(0L, 10L, "CURRENT", userId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByUserPastState() {
        Long userId = booker.getId();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking = bookingRepository.save(booking);

        List<BookingDtoForAnswer> bookings = bookingService.getByUser(0L, 10L, "PAST", userId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByUserFutureState() {
        Long userId = booker.getId();
        List<BookingDtoForAnswer> bookings = bookingService.getByUser(0L, 10L, "FUTURE", userId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByUserWaitingState() {
        Long userId = booker.getId();
        List<BookingDtoForAnswer> bookings = bookingService.getByUser(0L, 10L, "WAITING", userId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByUserRejectedState() {
        Long userId = booker.getId();
        booking.setStatus(BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);

        List<BookingDtoForAnswer> bookings = bookingService.getByUser(0L, 10L, "REJECTED", userId);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    public void testGetByUserThrowsExceptionForInvalidPagination() {
        Long userId = booker.getId();

        assertThatThrownBy(() -> bookingService.getByUser(-1L, 10L, "ALL", userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");

        assertThatThrownBy(() -> bookingService.getByUser(0L, -1L, "ALL", userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");
    }

    @Test
    public void testGetByUserThrowsExceptionForUnknownState() {
        Long userId = booker.getId();
        assertThatThrownBy(() -> bookingService.getByUser(0L, 10L, "UNKNOWN", userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown state: UNKNOWN");
    }
}



