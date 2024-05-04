package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public Item add(Item item, Long userId) {
        userService.getUserById(userId);
        ItemDto itemDto = itemMapper.item2ItemDto(item, userId);
        ItemDto itemFromStorage = itemStorage.add(itemDto);
        return itemMapper.itemDto2Item(itemFromStorage);
    }

    @Override
    public Item update(Item item, Long id, Long userId) {
        userService.getUserById(userId);
        ItemDto itemToCheck = itemStorage.getById(id).get();
        if (itemToCheck.getUserId().equals(userId)) {
            Item itemToStore = recreateItem(item, id);
            ItemDto itemDto = itemMapper.item2ItemDto(itemToStore, userId);
            ItemDto itemFromStorage = itemStorage.update(itemDto);
            return itemMapper.itemDto2Item(itemFromStorage);
        }
        throw new ForbiddenException("");
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id);
    }

    @Override
    public List<Item> getItems(Long userId) {
        userService.getUserById(userId);
        Collection<ItemDto> itemsFromStorage = itemStorage.getAll();
        return itemsFromStorage.stream()
                .filter(itemDto -> itemDto.getUserId().equals(userId))
                .map(itemMapper::itemDto2Item)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long id) {
        ItemDto itemFromStorage = itemStorage.getById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        return itemMapper.itemDto2Item(itemFromStorage);
    }

    @Override
    public Collection<Item> search(String text) {
        if (StringUtils.hasText(text)) {
            return itemStorage.search(text).stream()
                    .map(itemMapper::itemDto2Item)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Item recreateItem(Item item, Long id) {
        Item itemById = getItemById(id);
        Item.ItemBuilder itemByIdBuilder = itemById.toBuilder();
        if (item.getName() != null) {
            itemByIdBuilder.name(item.getName());
        }
        if (item.getDescription() != null) {
            itemByIdBuilder.description(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemByIdBuilder.available(item.getAvailable());
        }
        if (item.getRequest() != null) {
            itemByIdBuilder.request(item.getRequest());
        }
        return itemByIdBuilder.build();
    }
}
