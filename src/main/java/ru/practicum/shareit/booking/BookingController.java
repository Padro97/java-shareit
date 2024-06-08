package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = Constants.BOOKINGS_PATH)
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingDtoForAnswer> add(@Valid @RequestBody BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) throws AccessDeniedException {
        return ResponseEntity.status(201).body(service.add(booking, userId));
    }

    @PatchMapping(Constants.BOOKING_ID_PATH)
    public ResponseEntity<BookingDtoForAnswer> approve(@PathVariable Long bookingId, @RequestParam Boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.approve(bookingId, approved, userId));
    }

    @GetMapping(Constants.BOOKING_ID_PATH)
    public ResponseEntity<BookingDtoForAnswer> getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.getById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoForAnswer>> getByUser(@RequestParam(required = false, defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getByUser(state, userId));
    }

    @GetMapping(Constants.OWNER)
    public ResponseEntity<List<BookingDtoForAnswer>> getByOwner(@RequestParam(required = false, defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getByOwner(state, userId));
    }
}
