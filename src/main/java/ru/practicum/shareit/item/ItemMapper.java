package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ItemMapper {
    ItemDto item2Dto(Item item);
}
