package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(Constants.ITEMS_PATH)
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> add(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.status(201).body(service.add(item, ownerId));
    }

    @PatchMapping(Constants.ITEM_ID_PATH)
    public ResponseEntity<ItemDto> update(@RequestBody ItemDtoForUpdate item, @RequestHeader("X-Sharer-User-Id") Long ownerId, @Positive @PathVariable Long itemId) throws AccessDeniedException {
        item.setId(itemId);
        return ResponseEntity.ok(service.update(item, ownerId));
    }

    @GetMapping
    public ResponseEntity<List<ItemBookingDto>> getAllBy(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(service.getAllByOwner(ownerId));
    }

    @GetMapping(Constants.ITEM_ID_PATH)
    public ResponseEntity<ItemBookingDto> getById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getById(itemId, userId));
    }

    @GetMapping(Constants.SEARCH_ITEMS_PATH)
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(service.search(text));
    }

    @PostMapping(Constants.COMMENTS)
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody CommentDto comment) {
        return ResponseEntity.ok(service.addComment(itemId, userId, comment));
    }
}
