package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForBookingItems;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private final ItemMapper mapper = ItemMapper.INSTANCE;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @Transactional
    @Override
    public ItemDto add(ItemDto item, Long ownerId) {
        UserDto owner = userService.getById(ownerId);
        item.setOwner(owner);
        Item res = mapper.toItem(item);

        if (item.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException("Запроса с таким ID не найдено"));
            res.setRequest(itemRequest);
        }

        return mapper.toDto(repository.save(res));
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

        for (CommentDto comment : comments) {
            comment.setAuthorName(comment.getAuthor().getName());
        }

        if (comments.isEmpty()) {
            result.setComments(new ArrayList<>());
        } else {
            result.setComments(comments);
        }

        return result;
    }

    @Override
    public List<ItemBookingDto> getAllByOwner(Long id, Long from, Long size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не могут быть отрицательными.");
        }
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<Item> items = repository.findAllByOwnerIdOrderByIdAsc(id, pageable).getContent();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        List<Booking> allBookings = bookingRepository.findAllByItem_IdIn(itemIds);
        List<Comment> allComments = commentRepository.findAllByItem_IdIn(itemIds);

        List<ItemBookingDto> result = new ArrayList<>();

        for (Item item : items) {
            Long itemId = item.getId();
            ItemBookingDto itemBookingDto = mapper.toItemDtoWithBooking(item);

            List<Booking> itemBookings = allBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(itemId))
                    .collect(Collectors.toList());

            BookingDtoForBookingItems lastBooking = findLastBooking(itemBookings, now);
            BookingDtoForBookingItems nextBooking = findNextBooking(itemBookings, now);

            itemBookingDto.setLastBooking(lastBooking);
            itemBookingDto.setNextBooking(nextBooking);

            List<CommentDto> comments = allComments.stream()
                    .filter(comment -> comment.getItem().getId().equals(itemId))
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());

            for (CommentDto comment : comments) {
                comment.setAuthorName(comment.getAuthor().getName());
            }

            itemBookingDto.setComments(comments.isEmpty() ? new ArrayList<>() : comments);
            result.add(itemBookingDto);
        }

        return result;
    }

    @Override
    public List<ItemDto> search(String text, Long from, Long size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не могут быть отрицательными.");
        }

        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());
        List<Item> items = repository.search(text, pageable).getContent();
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

    public BookingDtoForBookingItems findLastBooking(Long itemId, LocalDateTime now) {
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

    public BookingDtoForBookingItems findNextBooking(Long itemId, LocalDateTime now) {
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

    public List<RequestedItemDto> getAllByRequestId(Long requestId) {
        List<RequestedItemDto> res = mapper.toListRequestedItemDto(repository.findAllByRequestId(requestId));
        return res;
    }

    // Helper methods to reduce database calls
    private BookingDtoForBookingItems findLastBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(bookingMapper::toItemWithBookings)
                .filter(dto -> dto.getStatus() != BookingStatus.CANCELED && dto.getStatus() != BookingStatus.REJECTED)
                .findFirst()
                .orElse(null);
    }

    private BookingDtoForBookingItems findNextBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .sorted((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                .map(bookingMapper::toItemWithBookings)
                .filter(dto -> dto.getStatus() != BookingStatus.CANCELED && dto.getStatus() != BookingStatus.REJECTED)
                .findFirst()
                .orElse(null);
    }
}
