package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private final BookingMapper mapper = BookingMapper.INSTANCE;

    @Transactional
    @Override
    public BookingDtoForAnswer add(BookingDto bookingDto, Long userId) throws AccessDeniedException {
        User booker = userMapper.userDtoToUser(userService.getById(userId));
        Item item = itemMapper.toItemFromDtoWithBookings(itemService.getById(bookingDto.getItemId(), userId));
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if (start.isAfter(end)) {
            throw new ValidationException("Конец должен быть после старта!");
        }
        if (start.isEqual(end)) {
            throw new ValidationException("Конец не должен быть равен старту!");
        }
        if (start.isBefore(now)) {
            throw new ValidationException("Бронировать задним числом запрещено!");
        }
        if (end.isBefore(now)) {
            throw new ValidationException("Бронировать задним числом запрещено!");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать занятый предмет");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Нельзя забронировать свой предмет!");
        }

        Booking booking = mapper.fromDto(bookingDto);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);

        Booking savedBooking = repository.save(booking);

        return mapper.toAnswerDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingDtoForAnswer approve(Long id, Boolean isApproved, Long userId) throws AccessDeniedException {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Брони с таким ID не существует"));
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронь уже подтверждена!");
        }
        Item item = booking.getItem();
        Long ownerId = item.getOwner().getId();

        if (userId.equals(ownerId)) {
            if (isApproved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }

            Booking result = repository.save(booking);

            return mapper.toAnswerDto(result);
        } else {
            throw new AccessDeniedException("Подтвердить бронь может только владелец вещи!");
        }
    }

    @Override
    public BookingDtoForAnswer getById(Long id, Long userId) throws AccessDeniedException {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Брони с таким ID не существует"));
        Item item = booking.getItem();
        Long ownerId = item.getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (userId.equals(ownerId) || userId.equals(bookerId)) {
            return mapper.toAnswerDto(booking);
        } else {
            throw new AccessDeniedException("Подтвердить бронь может только владелец вещи!");
        }
    }

    @Override
    public List<BookingDtoForAnswer> getByUser(Long from, Long size, String state, Long userId) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не могут быть отрицательными.");
        }
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());
        userService.getById(userId);
        List<Booking> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        try {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    result = repository.findAllByBooker_IdOrderByStartDesc(userId, pageable).getContent();
                    break;
                case CURRENT:
                    result = repository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, pageable).getContent();
                    break;
                case PAST:
                    result = repository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, now, pageable).getContent();
                    break;
                case FUTURE:
                    result = repository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, now, pageable).getContent();
                    break;
                case WAITING:
                    result = repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable).getContent();
                    break;
                case REJECTED:
                    result = repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable).getContent();
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }


        return mapper.toListAnswerDto(result);
    }

    @Override
    public List<BookingDtoForAnswer> getByOwner(Long from, Long size, String state, Long ownerId) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не могут быть отрицательными.");
        }
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());
        userService.getById(ownerId);
        List<Booking> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        try {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    result = repository.findAllByItem_Owner_IdOrderByStartDesc(ownerId, pageable).getContent();
                    break;
                case CURRENT:
                    result = repository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, pageable).getContent();
                    break;
                case PAST:
                    result = repository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(ownerId, now, pageable).getContent();
                    break;
                case FUTURE:
                    result = repository.findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(ownerId, now, pageable).getContent();
                    break;
                case WAITING:
                    result = repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable).getContent();
                    break;
                case REJECTED:
                    result = repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable).getContent();
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        return mapper.toListAnswerDto(result);
    }
}
