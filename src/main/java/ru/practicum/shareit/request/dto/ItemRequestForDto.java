package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.RequestedItemDto;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestForDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<RequestedItemDto> items;
}
