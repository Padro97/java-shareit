package ru.practicum.shareit.item.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestedItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
