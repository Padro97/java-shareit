package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialize() throws Exception {
        UserDto author = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(null)
                .requestId(null)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("This is a comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.of(2023, 6, 12, 12, 0))
                .authorName("John Doe")
                .build();

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"This is a comment\"");
        assertThat(json).contains("\"item\":{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"owner\":null,\"available\":true,\"requestId\":null}");
        assertThat(json).contains("\"author\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}");
        assertThat(json).contains("\"created\":\"2023-06-12T12:00:00\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"text\":\"This is a comment\"," +
                "\"item\":{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"owner\":null,\"available\":true,\"requestId\":null}," +
                "\"author\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "\"created\":\"2023-06-12T12:00:00\",\"authorName\":\"John Doe\"}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("This is a comment");
        assertThat(commentDto.getItem().getId()).isEqualTo(1L);
        assertThat(commentDto.getItem().getName()).isEqualTo("Item Name");
        assertThat(commentDto.getItem().getDescription()).isEqualTo("Item Description");
        assertThat(commentDto.getItem().getAvailable()).isTrue();
        assertThat(commentDto.getItem().getOwner()).isNull();
        assertThat(commentDto.getItem().getRequestId()).isNull();
        assertThat(commentDto.getAuthor().getId()).isEqualTo(1L);
        assertThat(commentDto.getAuthor().getName()).isEqualTo("John Doe");
        assertThat(commentDto.getAuthor().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 6, 12, 12, 0));
        assertThat(commentDto.getAuthorName()).isEqualTo("John Doe");
    }
}