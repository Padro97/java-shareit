package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForDto;

import java.util.List;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    ItemRequest fromDto(ItemRequestDto dto);

    ItemRequestDto toDto(ItemRequest request);

    List<ItemRequestDto> toListDto(List<ItemRequest> requests);

    ItemRequestForDto toGetDto(ItemRequest request);

    List<ItemRequestForDto> toListGetDto(List<ItemRequest> requests);
}
