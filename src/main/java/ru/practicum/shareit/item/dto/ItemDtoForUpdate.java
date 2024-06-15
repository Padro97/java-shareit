package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoForUpdate {
    private Long id;

    private String name;

    private String description;

    private UserDto owner;

    private Boolean available;

    private ItemRequestDto request;
}
