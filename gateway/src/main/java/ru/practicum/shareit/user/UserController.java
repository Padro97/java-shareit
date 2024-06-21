package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PathConstants;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(PathConstants.USERS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Запрос на получение списка всех пользователей");
        return userClient.findAll();
    }

    @GetMapping(PathConstants.BY_ID)
    public ResponseEntity<Object> findById(@PathVariable long id) {
        log.info("Запрос на получение пользователя c id {}", id);
        return userClient.findById(id);
    }

    @PatchMapping(PathConstants.BY_ID)
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UserDto user) {
        log.info("Запрос на обновление пользователя с id {}", id);
        return userClient.update(id, user);
    }

    @DeleteMapping(PathConstants.BY_ID)
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        return userClient.delete(id);
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserDto user) {
        log.info("Запрос на добавление нового пользователя {}", user);
        return userClient.save(user);
    }
}
