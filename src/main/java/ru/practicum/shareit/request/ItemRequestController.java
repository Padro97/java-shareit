package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = Constants.REQUEST_PATH)
public class ItemRequestController {
    private final RequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> add(@Valid @RequestBody ItemRequestDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(201).body(service.add(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestForDto>> getByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }

    @GetMapping(path = Constants.ALL)
    public ResponseEntity<List<ItemRequestDto>> getAll(@RequestParam(defaultValue = "0") Long from,
                                                       @RequestParam(defaultValue = "100") Long size,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getAll(from, size, userId));
    }

    @GetMapping(path = Constants.BY_ID)
    public ResponseEntity<ItemRequestForDto> getById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getById(requestId, userId));
    }
}
