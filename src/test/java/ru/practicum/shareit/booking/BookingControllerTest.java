package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForAnswer;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private BookingDtoForAnswer bookingDtoForAnswer;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingDtoForAnswer = BookingDtoForAnswer.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void testAdd() throws Exception {
        Mockito.when(bookingService.add(any(BookingDto.class), anyLong())).thenReturn(bookingDtoForAnswer);

        mockMvc.perform(post(Constants.BOOKINGS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDtoForAnswer.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoForAnswer.getStatus().toString()));
    }

    @Test
    void testApprove() throws Exception {
        Mockito.when(bookingService.approve(anyLong(), anyBoolean(), anyLong())).thenReturn(bookingDtoForAnswer);

        mockMvc.perform(patch(Constants.BOOKINGS_PATH + "/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoForAnswer.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoForAnswer.getStatus().toString()));
    }

    @Test
    void testGetById() throws Exception {
        Mockito.when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDtoForAnswer);

        mockMvc.perform(get(Constants.BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoForAnswer.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoForAnswer.getStatus().toString()));
    }

    @Test
    void testGetByUser() throws Exception {
        List<BookingDtoForAnswer> bookings = Collections.singletonList(bookingDtoForAnswer);
        Mockito.when(bookingService.getByUser(anyLong(), anyLong(), anyString(), anyLong())).thenReturn(bookings);

        mockMvc.perform(get(Constants.BOOKINGS_PATH)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoForAnswer.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoForAnswer.getStatus().toString()));
    }

    @Test
    void testGetByOwner() throws Exception {
        List<BookingDtoForAnswer> bookings = Collections.singletonList(bookingDtoForAnswer);
        Mockito.when(bookingService.getByOwner(anyLong(), anyLong(), anyString(), anyLong())).thenReturn(bookings);

        mockMvc.perform(get(Constants.BOOKINGS_PATH + "/owner")
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoForAnswer.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoForAnswer.getStatus().toString()));
    }
}