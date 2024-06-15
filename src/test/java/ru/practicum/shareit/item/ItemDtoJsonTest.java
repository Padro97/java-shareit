package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialize() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .owner(owner)
                .available(true)
                .requestId(2L)
                .build();

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"owner\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":2");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\"," +
                "\"owner\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "\"available\":true,\"requestId\":2}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemDto.getOwner().getId()).isEqualTo(1L);
        assertThat(itemDto.getOwner().getName()).isEqualTo("John Doe");
        assertThat(itemDto.getOwner().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(2L);
    }
}
