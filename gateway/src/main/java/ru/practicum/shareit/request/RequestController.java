package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Checks;
import ru.practicum.shareit.PathConstants;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(PathConstants.REQUESTS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody @Valid RequestDto request) {
        log.info("Сохранение запроса");
        return requestClient.save(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех запросов переданного пользователя");
        return requestClient.findAllUserRequests(userId);
    }

    @GetMapping(PathConstants.ALL)
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Checks.pageParams(from, size);
        log.info("Получение все запросов других пользователей, на которые можно ответить");
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping(PathConstants.BY_ID)
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long id) {
        log.info("Получение запроса по id");
        return requestClient.findById(userId, id);
    }
}
