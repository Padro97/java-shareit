package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dto.RequestedItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialize() throws Exception {
        UserDto requester = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        RequestedItemDto item = RequestedItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .requestId(2L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Request Description")
                .requester(requester)
                .created(LocalDateTime.of(2023, 6, 12, 12, 0))
                .items(Arrays.asList(item))
                .build();

        String json = objectMapper.writeValueAsString(itemRequestDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Request Description\"");
        assertThat(json).contains("\"requester\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}");
        assertThat(json).contains("\"created\":\"2023-06-12T12:00:00\"");
        assertThat(json).contains("\"items\":[{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"requestId\":2}]");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"description\":\"Request Description\"," +
                "\"requester\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "\"created\":\"2023-06-12T12:00:00\"," +
                "\"items\":[{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"requestId\":2}]}";

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Request Description");
        assertThat(itemRequestDto.getRequester().getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getRequester().getName()).isEqualTo("John Doe");
        assertThat(itemRequestDto.getRequester().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 6, 12, 12, 0));
        assertThat(itemRequestDto.getItems()).hasSize(1);
        assertThat(itemRequestDto.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getItems().get(0).getName()).isEqualTo("Item Name");
        assertThat(itemRequestDto.getItems().get(0).getDescription()).isEqualTo("Item Description");
        assertThat(itemRequestDto.getItems().get(0).getAvailable()).isTrue();
        assertThat(itemRequestDto.getItems().get(0).getRequestId()).isEqualTo(2L);
    }
}
