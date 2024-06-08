package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    ItemRequest fromDto(ItemRequestDto dto);

    ItemRequestDto toDto(ItemRequest request);
}
