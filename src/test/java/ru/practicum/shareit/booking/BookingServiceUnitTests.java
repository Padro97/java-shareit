package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BookingServiceUnitTests {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private ItemMapper itemMapper = ItemMapper.INSTANCE;
    private UserMapper userMapper = UserMapper.INSTANCE;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddBookingSuccess() throws AccessDeniedException {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        UserDto ownerDto = new UserDto(2L, "Owner", "owner@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        itemDto.setOwner(ownerDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        User user = userMapper.userDtoToUser(userDto);
        Item item = itemMapper.toItemFromDtoWithBookings(itemDto);

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        BookingDtoForAnswer result = bookingService.add(bookingDto, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void testAddBookingItemNotAvailable() {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        UserDto ownerDto = new UserDto(2L, "Owner", "owner@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(false);
        itemDto.setOwner(ownerDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.add(bookingDto, userId));
    }

    @Test
    public void testAddBookingOwnItem() {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        itemDto.setOwner(userDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        assertThrows(AccessDeniedException.class, () -> bookingService.add(bookingDto, userId));
    }

    @Test
    public void testAddBookingStartAfterEnd() {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        UserDto ownerDto = new UserDto(2L, "Owner", "owner@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        itemDto.setOwner(ownerDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.add(bookingDto, userId));
    }

    @Test
    public void testAddBookingStartEqualsEnd() {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        UserDto ownerDto = new UserDto(2L, "Owner", "owner@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        itemDto.setOwner(ownerDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);

        LocalDateTime fixedTime = LocalDateTime.of(2023, 6, 15, 12, 0);
        bookingDto.setStart(fixedTime);
        bookingDto.setEnd(fixedTime);

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.add(bookingDto, userId));
    }

    @Test
    public void testAddBookingStartBeforeNow() {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        UserDto ownerDto = new UserDto(2L, "Owner", "owner@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        itemDto.setOwner(ownerDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.add(bookingDto, userId));
    }

    @Test
    public void testAddBookingEndBeforeNow() {
        Long userId = 1L;
        Long itemId = 2L;
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        UserDto ownerDto = new UserDto(2L, "Owner", "owner@example.com");
        ItemBookingDto itemDto = new ItemBookingDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);
        itemDto.setOwner(ownerDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));

        when(userService.getById(userId)).thenReturn(userDto);
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.add(bookingDto, userId));
    }

    @Test
    public void testApproveBookingSuccess() throws AccessDeniedException {
        Long userId = 1L;
        Long bookingId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoForAnswer result = bookingService.approve(bookingId, true, userId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void testApproveBookingAccessDenied() {
        Long userId = 1L;
        Long bookingId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.approve(bookingId, true, userId));
    }

    @Test
    public void testGetByIdSuccess() throws AccessDeniedException {
        Long userId = 1L;
        Long bookingId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User();
        booker.setId(userId);
        booking.setBooker(booker);
        Item item = new Item();
        item.setOwner(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoForAnswer result = bookingService.getById(bookingId, userId);

        assertThat(result).isNotNull();
    }

    @Test
    public void testGetByIdAccessDenied() {
        Long userId = 1L;
        Long bookingId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);
        Item item = new Item();
        User owner = new User();
        owner.setId(3L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.getById(bookingId, userId));
    }

    @Test
    public void testGetByUserSuccess() {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Booking booking = new Booking();
        booking.setId(1L);

        UserDto userDto = new UserDto(1L, "User", "user@example.com");

        when(userService.getById(userId)).thenReturn(userDto);
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDtoForAnswer> result = bookingService.getByUser((long) from, (long) size, state, userId);

        assertThat(result).isNotEmpty();
        verify(bookingRepository, times(1)).findAllByBooker_IdOrderByStartDesc(eq(userId), any(Pageable.class));
    }

    @Test
    public void testGetByOwnerThrowsExceptionForInvalidPagination() {
        Long ownerId = 1L;
        assertThatThrownBy(() -> bookingService.getByOwner(-1L, 10L, "ALL", ownerId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");

        assertThatThrownBy(() -> bookingService.getByOwner(0L, -1L, "ALL", ownerId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");
    }

    @Test
    public void testGetByOwnerThrowsExceptionForUnknownState() {
        Long ownerId = 1L;
        assertThatThrownBy(() -> bookingService.getByOwner(0L, 10L, "UNKNOWN", ownerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown state: UNKNOWN");
    }

    @Test
    public void testGetByOwnerAllState() {
        Long ownerId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Booking booking = new Booking();
        booking.setId(1L);

        UserDto ownerDto = new UserDto(1L, "Owner", "owner@example.com");

        when(userService.getById(ownerId)).thenReturn(ownerDto);
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(eq(ownerId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDtoForAnswer> result = bookingService.getByOwner((long) from, (long) size, "ALL", ownerId);

        assertThat(result).isNotEmpty();
        verify(bookingRepository, times(1)).findAllByItem_Owner_IdOrderByStartDesc(eq(ownerId), any(Pageable.class));
    }

    @Test
    public void testGetByOwnerWaitingState() {
        Long ownerId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Booking booking = new Booking();
        booking.setId(1L);

        UserDto ownerDto = new UserDto(1L, "Owner", "owner@example.com");

        when(userService.getById(ownerId)).thenReturn(ownerDto);
        when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(eq(ownerId), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDtoForAnswer> result = bookingService.getByOwner((long) from, (long) size, "WAITING", ownerId);

        assertThat(result).isNotEmpty();
        verify(bookingRepository, times(1)).findAllByItem_Owner_IdAndStatusOrderByStartDesc(eq(ownerId), eq(BookingStatus.WAITING), any(Pageable.class));
    }

    @Test
    public void testGetByOwnerRejectedState() {
        Long ownerId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Booking booking = new Booking();
        booking.setId(1L);

        UserDto ownerDto = new UserDto(1L, "Owner", "owner@example.com");

        when(userService.getById(ownerId)).thenReturn(ownerDto);
        when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(eq(ownerId), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDtoForAnswer> result = bookingService.getByOwner((long) from, (long) size, "REJECTED", ownerId);

        assertThat(result).isNotEmpty();
        verify(bookingRepository, times(1)).findAllByItem_Owner_IdAndStatusOrderByStartDesc(eq(ownerId), eq(BookingStatus.REJECTED), any(Pageable.class));
    }
}