package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private HashMap<Long, ItemDto> items = new HashMap<>();
    private Long idCounter = 0L;


    @Override
    public ItemDto add(ItemDto item, Long ownerId) {
        if (item.getName() == null || item.getName().isEmpty() || item.getAvailable() == null || item.getDescription() == null) {
            throw new ValidationException("Предмет не может иметь имя, статус доступа и описания со значением null");
        }

        item.setOwner(userService.getById(ownerId));
        idCounter++;
        item.setId(idCounter);

        items.put(idCounter, item);

        return item;
    }

    @Override
    public ItemDto update(ItemDto updatedItem, Long ownerId) throws AccessDeniedException {
        Long id = updatedItem.getId();

        if (items.containsKey(id)) {
            ItemDto existingItem = getById(id);

            if (!existingItem.getOwner().getId().equals(ownerId)) {
                throw new AccessDeniedException("Вещь может редактировать только владелец");
            }

            if (updatedItem.getName() != null) {
                existingItem.setName(updatedItem.getName());
            }
            if (updatedItem.getDescription() != null) {
                existingItem.setDescription(updatedItem.getDescription());
            }
            if (updatedItem.getAvailable() != null) {
                existingItem.setAvailable(updatedItem.getAvailable());
            }

            return existingItem;
        } else {
            throw new NoSuchElementException("Нет предмета с таким id");
        }

    }


    @Override
    public ItemDto getById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new IllegalArgumentException("Предмета с таким ID не существует");
        }
    }

    @Override
    public List<ItemDto> getAllByOwner(Long id) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        String lowerText = text.toLowerCase();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerText) || item.getDescription().toLowerCase().contains(lowerText))
                .filter(ItemDto::getAvailable)
                .collect(Collectors.toList());
    }
}
