package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto update(UserDtoForUpdate userDto);

    void remove(Long id);

    List<UserDto> getAll();

    UserDto getById(Long id);
}
