package ru.practicum.shareit.item;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDto2Item(ItemDto itemDto);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "available", source = "item.available", defaultValue = "false")
    ItemDto item2ItemDto(Item item, Long userId);

    default ItemDto mapContext(Item item, @Context Long userId) {
        return item2ItemDto(item, userId);
    }

}
