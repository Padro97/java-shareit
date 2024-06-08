package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private final ItemMapper mapper = ItemMapper.INSTANCE;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;


    @Transactional
    @Override
    public ItemDto add(ItemDto item, Long ownerId) {
        UserDto owner = userService.getById(ownerId);
        item.setOwner(owner);
        return mapper.toDto(repository.save(mapper.toItem(item)));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDtoForUpdate updatedItem, Long ownerId) throws AccessDeniedException {
        Long id = updatedItem.getId();

        Item existingItem = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Предмета с таким ID не существует"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Вещь может редактировать только владелец");
        }

        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            existingItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            existingItem.setAvailable(updatedItem.getAvailable());
        }

        return mapper.toDto(repository.save(existingItem));
    }

    @Override
    public ItemBookingDto getById(Long itemId, Long userId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с таким ID не существует"));
        ItemBookingDto result = mapper.toItemDtoWithBooking(item);

        if (userId.equals(item.getOwner().getId())) {
            LocalDateTime now = LocalDateTime.now();

            BookingDtoForBookingItems lastBooking = findLastBooking(itemId, now);
            BookingDtoForBookingItems nextBooking = findNextBooking(itemId, now);

            result.setLastBooking(lastBooking);
            result.setNextBooking(nextBooking);
        }

        List<CommentDto> comments = commentMapper.toDtoList(commentRepository.findAllByItem_Id(itemId));
        comments.forEach(comment -> comment.setAuthorName(comment.getAuthor().getName()));
        result.setComments(comments.isEmpty() ? new ArrayList<>() : comments);

        return result;
    }

    @Override
    public List<ItemBookingDto> getAllByOwner(Long ownerId) {
        List<Item> items = repository.findAllByOwnerIdOrderByIdAsc(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Booking>> bookingsByItemId = bookingRepository.findAll()
                .stream()
                .filter(booking -> itemIds.contains(booking.getItem().getId()))
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findAll()
                .stream()
                .filter(comment -> itemIds.contains(comment.getItem().getId()))
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemBookingDto> result = new ArrayList<>();

        for (Item item : items) {
            Long itemId = item.getId();
            ItemBookingDto itemBookingDto = mapper.toItemDtoWithBooking(item);

            List<Booking> itemBookings = bookingsByItemId.getOrDefault(itemId, Collections.emptyList());
            BookingDtoForBookingItems lastBooking = findLastBooking(itemId, now);
            BookingDtoForBookingItems nextBooking = findNextBooking(itemId, now);

            itemBookingDto.setLastBooking(lastBooking);
            itemBookingDto.setNextBooking(nextBooking);

            List<CommentDto> comments = commentsByItemId.getOrDefault(itemId, new ArrayList<>())
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
            comments.forEach(comment -> comment.setAuthorName(comment.getAuthor().getName()));
            itemBookingDto.setComments(comments.isEmpty() ? new ArrayList<>() : comments);

            result.add(itemBookingDto);
        }

        return result;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = repository.search(text);
        return mapper.itemsToItemDto(items);
    }

    @Transactional
    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с таким ID не существует"));
        UserDto user = userService.getById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId, userId, now, BookingStatus.APPROVED)) {
            commentDto.setItem(mapper.toDto(item));
            commentDto.setAuthor(user);
            commentDto.setCreated(now);

            Comment savedComment = commentRepository.save(commentMapper.fromDto(commentDto));

            CommentDto result = commentMapper.toDto(savedComment);
            result.setAuthorName(user.getName());
            return result;
        } else {
            throw new IllegalStateException("Нельзя оставить комментарий к предмету, который Вы не бронировали");
        }
    }

    private BookingDtoForBookingItems findLastBooking(Long itemId, LocalDateTime now) {
        List<Booking> lastBookings = bookingRepository.findAllByItem_IdAndStartIsBeforeOrderByStartDesc(itemId, now);

        if (!lastBookings.isEmpty()) {
            BookingDtoForBookingItems result = bookingMapper.toItemWithBookings(lastBookings.get(0));
            BookingStatus status = result.getStatus();

            if (status != BookingStatus.CANCELED && status != BookingStatus.REJECTED) {
                result.setBookerId(result.getBooker().getId());
                return result;
            } else return null;

        }
        return null;
    }

    private BookingDtoForBookingItems findNextBooking(Long itemId, LocalDateTime now) {
        List<Booking> nextBookings = bookingRepository.findAllByItem_idAndStartIsAfterOrderByStartAsc(itemId, now);
        if (!nextBookings.isEmpty()) {
            BookingDtoForBookingItems result = bookingMapper.toItemWithBookings(nextBookings.get(0));
            BookingStatus status = result.getStatus();

            if (status != BookingStatus.CANCELED && status != BookingStatus.REJECTED) {
                result.setBookerId(result.getBooker().getId());
                return result;
            } else return null;

        }
        return null;
    }
}
