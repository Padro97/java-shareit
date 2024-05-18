package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.Constants;

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
    public ResponseEntity<ItemDto> add(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.status(201).body(service.add(item, ownerId));
    }

    @PatchMapping(Constants.ITEM_ID_PATH)
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long ownerId, @Positive @PathVariable Long itemId) throws AccessDeniedException {
        item.setId(itemId);
        return ResponseEntity.ok(service.update(item, ownerId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllBy(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(service.getAllByOwner(ownerId));
    }

    @GetMapping(Constants.ITEM_ID_PATH)
    public ResponseEntity<ItemDto> getById(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.getById(itemId));
    }

    @GetMapping(Constants.SEARCH_ITEMS_PATH)
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(service.search(text));
    }
}

