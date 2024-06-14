package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ItemServiceUnitTests {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddItem() {
        Long ownerId = 1L;
        User owner = User.builder().id(ownerId).name("John").email("john@example.com").build();
        UserDto ownerDto = UserDto.builder().id(ownerId).name("John").email("john@example.com").build();
        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        Item item = Item.builder().name("Item").description("Description").available(true).owner(owner).build();

        when(userService.getById(ownerId)).thenReturn(ownerDto);
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.add(itemDto, ownerId);

        assertThat(result).isNotNull();
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void testUpdateItem() throws AccessDeniedException {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder().id(itemId).name("Updated Item").build();
        User owner = User.builder().id(ownerId).name("John").email("john@example.com").build();
        Item existingItem = Item.builder().id(itemId).name("Old Item").owner(owner).build();
        Item updatedItem = Item.builder().id(itemId).name("Updated Item").owner(owner).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.toDto(any(Item.class))).thenReturn(ItemDto.builder().name("Updated Item").build());

        ItemDto result = itemService.update(itemDtoForUpdate, ownerId);

        assertThat(result).isNotNull();
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void testUpdateItemThrowsAccessDeniedException() {
        Long ownerId = 1L;
        Long itemId = 1L;
        Long otherOwnerId = 2L;
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder().id(itemId).build();
        User otherOwner = User.builder().id(otherOwnerId).name("Jane").email("jane@example.com").build();
        Item existingItem = Item.builder().id(itemId).owner(otherOwner).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> itemService.update(itemDtoForUpdate, ownerId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Вещь может редактировать только владелец");
    }

    @Test
    public void testGetById() {
        Long itemId = 1L;
        Long userId = 1L;
        User owner = User.builder().id(userId).name("John").email("john@example.com").build();
        Item item = Item.builder().id(itemId).owner(owner).build();
        ItemBookingDto itemBookingDto = ItemBookingDto.builder().id(itemId).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDtoWithBooking(any(Item.class))).thenReturn(itemBookingDto);
        when(bookingMapper.toItemWithBookings(any(Booking.class)))
                .thenReturn(BookingDtoForBookingItems.builder().id(1L).booker(UserDto.builder().id(2L).build()).build());

        ItemBookingDto result = itemService.getById(itemId, userId);

        assertThat(result).isNotNull();
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    public void testGetByIdThrowsNoSuchElementException() {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(itemId, userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Предмета с таким ID не существует");
    }

    @Test
    public void testGetAllByOwner() {
        Long ownerId = 1L;
        Long from = 0L;
        Long size = 10L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size.intValue());
        List<Item> items = Collections.singletonList(Item.builder().id(1L).build());
        Page<Item> pageItems = new PageImpl<>(items);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(eq(ownerId), any(Pageable.class))).thenReturn(pageItems);
        when(itemMapper.toItemDtoWithBooking(any(Item.class))).thenReturn(ItemBookingDto.builder().id(1L).build());
        when(bookingMapper.toItemWithBookings(any(Booking.class)))
                .thenReturn(BookingDtoForBookingItems.builder().id(1L).booker(UserDto.builder().id(2L).build()).build());

        List<ItemBookingDto> result = itemService.getAllByOwner(ownerId, from, size);

        assertThat(result).isNotEmpty();
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(eq(ownerId), any(Pageable.class));
    }

    @Test
    public void testGetAllByOwnerWithNegativePaginationParameters() {
        Long ownerId = 1L;

        assertThatThrownBy(() -> itemService.getAllByOwner(ownerId, -1L, 10L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");
    }

    @Test
    public void testSearchItems() {
        String text = "item";
        Long from = 0L;
        Long size = 10L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size.intValue());
        List<Item> items = Collections.singletonList(Item.builder().id(1L).build());
        Page<Item> pageItems = new PageImpl<>(items);

        when(itemRepository.search(eq(text), any(Pageable.class))).thenReturn(pageItems);
        when(itemMapper.itemsToItemDto(anyList())).thenReturn(Collections.singletonList(ItemDto.builder().id(1L).build()));

        List<ItemDto> result = itemService.search(text, from, size);

        assertThat(result).isNotEmpty();
        verify(itemRepository, times(1)).search(eq(text), any(Pageable.class));
    }

    @Test
    public void testSearchItemsWithEmptyText() {
        List<ItemDto> result = itemService.search("", 0L, 10L);

        assertThat(result).isEmpty();
        verify(itemRepository, times(0)).search(anyString(), any(Pageable.class));
    }

    @Test
    public void testSearchItemsWithNegativePaginationParameters() {
        assertThatThrownBy(() -> itemService.search("item", -1L, 10L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");
    }

    @Test
    public void testAddComment() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = CommentDto.builder().text("Great item!").build();
        Item item = Item.builder().id(itemId).build();
        User user = User.builder().id(userId).name("John").build();
        Comment comment = Comment.builder().text("Great item!").item(item).author(user).created(LocalDateTime.now()).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).name("John").build());
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(eq(itemId), eq(userId), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(true);
        when(commentMapper.fromDto(any(CommentDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = itemService.addComment(itemId, userId, commentDto);

        assertThat(result).isNotNull();
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testAddCommentThrowsIllegalStateException() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = CommentDto.builder().text("Great item!").build();
        Item item = Item.builder().id(itemId).build();
        User user = User.builder().id(userId).name("John").build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).name("John").build());
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(eq(itemId), eq(userId), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, commentDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Нельзя оставить комментарий к предмету, который Вы не бронировали");
    }

    @Test
    public void testFindLastBookingReturnsNullIfNoValidBooking() {
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder().id(1L).start(now.minusDays(1)).status(BookingStatus.CANCELED).build();
        BookingDtoForBookingItems bookingDto = BookingDtoForBookingItems.builder().id(1L).status(BookingStatus.CANCELED).booker(UserDto.builder().id(2L).build()).build();

        when(bookingRepository.findAllByItem_IdAndStartIsBeforeOrderByStartDesc(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toItemWithBookings(any(Booking.class))).thenReturn(bookingDto);

        BookingDtoForBookingItems result = itemService.findLastBooking(itemId, now);

        assertNull(result);
        verify(bookingRepository, times(1)).findAllByItem_IdAndStartIsBeforeOrderByStartDesc(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    public void testFindLastBookingReturnsNullIfNoBookings() {
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();

        when(bookingRepository.findAllByItem_IdAndStartIsBeforeOrderByStartDesc(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        BookingDtoForBookingItems result = itemService.findLastBooking(itemId, now);

        assertNull(result);
        verify(bookingRepository, times(1)).findAllByItem_IdAndStartIsBeforeOrderByStartDesc(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    public void testFindNextBookingReturnsNullIfNoValidBooking() {
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder().id(1L).start(now.plusDays(1)).status(BookingStatus.CANCELED).build();
        BookingDtoForBookingItems bookingDto = BookingDtoForBookingItems.builder().id(1L).status(BookingStatus.CANCELED).booker(UserDto.builder().id(2L).build()).build();

        when(bookingRepository.findAllByItem_idAndStartIsAfterOrderByStartAsc(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toItemWithBookings(any(Booking.class))).thenReturn(bookingDto);

        BookingDtoForBookingItems result = itemService.findNextBooking(itemId, now);

        assertNull(result);
        verify(bookingRepository, times(1)).findAllByItem_idAndStartIsAfterOrderByStartAsc(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    public void testFindNextBookingReturnsNullIfNoBookings() {
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();

        when(bookingRepository.findAllByItem_idAndStartIsAfterOrderByStartAsc(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        BookingDtoForBookingItems result = itemService.findNextBooking(itemId, now);

        assertNull(result);
        verify(bookingRepository, times(1)).findAllByItem_idAndStartIsAfterOrderByStartAsc(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    public void testGetAllByRequestId() {
        Long requestId = 1L;
        Item item = Item.builder().id(1L).build();
        RequestedItemDto requestedItemDto = RequestedItemDto.builder().id(1L).build();

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Collections.singletonList(item));
        when(itemMapper.toListRequestedItemDto(anyList())).thenReturn(Collections.singletonList(requestedItemDto));

        List<RequestedItemDto> result = itemService.getAllByRequestId(requestId);

        assertThat(result).isNotEmpty();
        verify(itemRepository, times(1)).findAllByRequestId(requestId);
    }
}
