package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Mapper(componentModel = "spring")
@Component("itemMapper")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto toDto(Item item);

    Item toItem(ItemDto item);

    List<ItemDto> itemsToItemDto(List<Item> items);

    List<ItemBookingDto> itemsToItemsDtoWithBooking(List<Item> items);

    ItemBookingDto toItemDtoWithBooking(Item item);

    Item toItemFromDtoWithBookings(ItemBookingDto item);
}
