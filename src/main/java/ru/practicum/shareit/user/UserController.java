package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(Constants.USERS_PATH)
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(Constants.USER_ID)
    public ResponseEntity<UserDto> getById(@Positive @PathVariable Long userId) {
        return ResponseEntity.ok(service.getById(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> add(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(201).body(service.add(userDto));
    }

    @PatchMapping(Constants.USER_ID)
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserDto userDto, @Positive @PathVariable Long userId) {
        userDto.setId(userId);
        return ResponseEntity.ok(service.update(userDto));
    }

    @DeleteMapping(Constants.USER_ID)
    public ResponseEntity remove(@PathVariable Long userId) {
        service.remove(userId);
        return ResponseEntity.status(204).body(null);
    }
}

