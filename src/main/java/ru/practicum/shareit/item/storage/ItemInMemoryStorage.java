package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Long, ItemDto> itemMap = new HashMap<>();
    private Long nextId = 0L;

    @Override
    public ItemDto add(ItemDto itemDto) {
        log.info("Attempt to add item {}", itemDto);
        ItemDto itemWithId = itemDto.toBuilder()
                .id(getNextValidId())
                .build();
        itemMap.put(itemWithId.getId(), itemWithId);
        log.info("Item with id={} successfully added", itemWithId.getId());
        return itemWithId;
    }

    @Override
    public ItemDto update(ItemDto itemDto) {
        Long itemId = itemDto.getId();
        if (itemMap.containsKey(itemId)) {
            log.info("Attempt to change item with id={}", itemId);
            itemMap.put(itemId, itemDto);
            log.info("Item with id={} successfully updated", itemId);
            return itemDto;
        }
        log.warn("Item with id={} is not present", itemId);
        throw new ItemNotFoundException(itemId);
    }

    @Override
    public void delete(Long id) {
        if (itemMap.containsKey(id)) {
            log.info("Attempt to delete item with id={}", id);
            itemMap.remove(id);
            log.info("Item with id={} successfully deleted", id);
        }
        log.warn("Item with id={} is not present", id);
        throw new ItemNotFoundException(id);
    }

    @Override
    public Collection<ItemDto> getAll() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Optional<ItemDto> getById(Long id) {
        return Optional.ofNullable(itemMap.get(id));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        String textLowerCase = text.toLowerCase();
        return itemMap.values().stream()
                .filter(itemDto -> itemDto.getName().toLowerCase().contains(textLowerCase)
                        || itemDto.getDescription().toLowerCase().contains(textLowerCase))
                .filter(ItemDto::isAvailable)
                .collect(Collectors.toList());
    }

    private Long getNextValidId() {
        nextId++;
        return nextId;
    }
}
