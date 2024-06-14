package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto item, Long ownerId);

    ItemDto update(ItemDtoForUpdate item, Long ownerId) throws AccessDeniedException;

    ItemBookingDto getById(Long id, Long userId);

    List<ItemBookingDto> getAllByOwner(Long ownerId, Long from, Long size);

    List<ItemDto> search(String text, Long from, Long size);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

    List<RequestedItemDto> getAllByRequestId(Long requestId);
}
