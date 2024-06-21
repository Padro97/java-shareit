package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(ItemRequestDto requestDto, Long userId);

    List<ItemRequestForDto> getByUser(Long userId);

    List<ItemRequestDto> getAll(Long from, Long size, Long userId);

    ItemRequestForDto getById(Long requestId, Long userId);
}
