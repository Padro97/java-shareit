package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto update(UserDto userDto);

    void remove(Long id);

    List<UserDto> getAll();

    UserDto getById(Long id);
}
