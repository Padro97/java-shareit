package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 6, 12, 12, 0))
                .end(LocalDateTime.of(2023, 6, 13, 12, 0))
                .itemId(1L)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .build();

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2023-06-12T12:00:00\"");
        assertThat(json).contains("\"end\":\"2023-06-13T12:00:00\"");
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"booker\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}");
        assertThat(json).contains("\"status\":\"APPROVED\"");
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"start\":\"2023-06-12T12:00:00\",\"end\":\"2023-06-13T12:00:00\"," +
                "\"itemId\":1,\"booker\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "\"status\":\"APPROVED\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2023, 6, 12, 12, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2023, 6, 13, 12, 0));
        assertThat(bookingDto.getItemId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("John Doe");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
