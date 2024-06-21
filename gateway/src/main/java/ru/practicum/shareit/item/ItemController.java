package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Checks;
import ru.practicum.shareit.PathConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(PathConstants.ITEMS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос на получение списка всех предметов");
        return itemClient.findAllUserItems(userId, from, size);
    }

    @GetMapping(PathConstants.BY_ID)
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long id) {
        log.info("Запрос на получение предмета с id {}", id);
        return itemClient.findById(userId, id);
    }

    @PatchMapping(PathConstants.BY_ID)
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long id, @RequestBody ItemDto item) {
        log.info("Запрос на обновление предмета с id {} пользователем с userId {}", id, userId);
        return itemClient.update(userId, id, item);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody ItemDto item) {
        log.info("Запрос на добавление нового предмета пользователем с userId {}", userId);
        return itemClient.save(userId, item);
    }

    @GetMapping(PathConstants.ITEMS_SEARCH)
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Checks.pageParams(from, size);
        log.info("Запрос на поиск предмета по названию или описанию");
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping(PathConstants.BY_ID + PathConstants.ITEMS_COMMENTS)
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long id,
                                             @Valid @RequestBody CommentDto comment) {
        log.info("Запрос на добавление комментария пользователем с userId {}", userId);
        return itemClient.addComment(userId, id, comment);
    }
}