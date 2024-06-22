package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemApiPathConstants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDtoForUpdate itemDtoForUpdate;
    private ItemBookingDto itemBookingDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("Updated Item 1")
                .description("Updated Description 1")
                .available(true)
                .build();

        itemBookingDto = ItemBookingDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Comment 1")
                .authorName("User 1")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void testAdd() throws Exception {
        when(itemService.add(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post(ItemApiPathConstants.ITEMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"name\": \"Item 1\", \"description\": \"Description 1\", \"available\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1)).add(any(ItemDto.class), anyLong());
    }

    @Test
    void testUpdate() throws Exception {
        when(itemService.update(any(ItemDtoForUpdate.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch(ItemApiPathConstants.ITEMS_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"name\": \"Updated Item 1\", \"description\": \"Updated Description 1\", \"available\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1)).update(any(ItemDtoForUpdate.class), anyLong());
    }

    @Test
    void testGetAllBy() throws Exception {
        List<ItemBookingDto> items = Arrays.asList(itemBookingDto);
        when(itemService.getAllByOwner(anyLong(), anyLong(), anyLong())).thenReturn(items);

        mockMvc.perform(get(ItemApiPathConstants.ITEMS_PATH)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemBookingDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemBookingDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemBookingDto.getDescription()));

        verify(itemService, times(1)).getAllByOwner(anyLong(), anyLong(), anyLong());
    }

    @Test
    void testGetById() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemBookingDto);

        mockMvc.perform(get(ItemApiPathConstants.ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemBookingDto.getId()))
                .andExpect(jsonPath("$.name").value(itemBookingDto.getName()))
                .andExpect(jsonPath("$.description").value(itemBookingDto.getDescription()));

        verify(itemService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    void testSearch() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.search(anyString(), anyLong(), anyLong())).thenReturn(items);

        mockMvc.perform(get(ItemApiPathConstants.ITEMS_PATH + "/search")
                        .param("from", "0")
                        .param("size", "10")
                        .param("text", "Item 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()));

        verify(itemService, times(1)).search(anyString(), anyLong(), anyLong());
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post(ItemApiPathConstants.ITEMS_PATH + "/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\"text\": \"Comment 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}
