package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForDto;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private ItemRequestDto requestDto;
    private ItemRequestForDto requestForGetDto;

    @BeforeEach
    void setUp() {
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test Request")
                .created(LocalDateTime.now())
                .build();

        requestForGetDto = ItemRequestForDto.builder()
                .id(1L)
                .description("Test Request")
                .created(LocalDateTime.now())
                .items(Arrays.asList())
                .build();
    }

    @Test
    void testAdd() throws Exception {
        when(requestService.add(any(ItemRequestDto.class), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"description\": \"Test Request\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));

        verify(requestService, times(1)).add(any(ItemRequestDto.class), anyLong());
    }

    @Test
    void testGetByUser() throws Exception {
        List<ItemRequestForDto> requests = Arrays.asList(requestForGetDto);
        when(requestService.getByUser(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestForGetDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestForGetDto.getDescription()));

        verify(requestService, times(1)).getByUser(anyLong());
    }

    @Test
    void testGetAll() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(requestDto);
        when(requestService.getAll(anyLong(), anyLong(), anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        verify(requestService, times(1)).getAll(anyLong(), anyLong(), anyLong());
    }

    @Test
    void testGetById() throws Exception {
        when(requestService.getById(anyLong(), anyLong())).thenReturn(requestForGetDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestForGetDto.getId()))
                .andExpect(jsonPath("$.description").value(requestForGetDto.getDescription()));

        verify(requestService, times(1)).getById(anyLong(), anyLong());
    }
}
