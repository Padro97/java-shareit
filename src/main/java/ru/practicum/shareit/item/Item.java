package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
public class Item {

    @Nullable
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    @Nullable
    ItemRequest request;

}
