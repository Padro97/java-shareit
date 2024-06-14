package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceIntegrationTests {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Item item;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();

        user = userRepository.save(user);

        item = Item.builder()
                .name("Test Item")
                .description("Description of test item")
                .owner(user)
                .available(true)
                .build();

        item = itemRepository.save(item);

        booking = Booking.builder()
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        booking = bookingRepository.save(booking);

        itemRequest = ItemRequest.builder()
                .description("Test request")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        itemRequest = requestRepository.save(itemRequest);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void testGetAllByOwner() {
        List<ItemBookingDto> items = itemService.getAllByOwner(user.getId(), 0L, 10L);

        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getId()).isEqualTo(item.getId());
        assertThat(items.get(0).getLastBooking()).isNotNull();
        assertThat(items.get(0).getNextBooking()).isNull();
    }

    @Test
    public void testAddItemWithRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item with Request")
                .description("Description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        ItemDto result = itemService.add(itemDto, user.getId());

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    public void testAddItemWithNonExistentRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item with Non-existent Request")
                .description("Description")
                .available(true)
                .requestId(999L) // Non-existent request ID
                .build();

        assertThrows(NoSuchElementException.class, () -> {
            itemService.add(itemDto, user.getId());
        });
    }

    @Test
    public void testUpdateItemDescriptionAndAvailability() throws AccessDeniedException {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Description")
                .available(true)
                .build();

        ItemDto addedItem = itemService.add(itemDto, user.getId());

        ItemDtoForUpdate updateDto = ItemDtoForUpdate.builder()
                .id(addedItem.getId())
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.update(updateDto, user.getId());

        assertNotNull(updatedItem);
        assertEquals(updateDto.getDescription(), updatedItem.getDescription());
        assertEquals(updateDto.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    public void testFindNextBooking() {
        User booker = User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();
        booker = userRepository.save(booker);

        Booking nextBooking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.WAITING)
                .build();
        nextBooking = bookingRepository.save(nextBooking);

        BookingDtoForBookingItems result = itemService.findNextBooking(item.getId(), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(nextBooking.getId(), result.getId());
        assertEquals(booker.getId(), result.getBookerId());
    }

    @Test
    public void testFindNextBookingNoBookings() {
        BookingDtoForBookingItems result = itemService.findNextBooking(item.getId(), LocalDateTime.now().plusDays(10));

        assertNull(result);
    }
}