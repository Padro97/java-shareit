package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UserDto updatedUserDto;
    private UserDtoForUpdate userDtoForUpdate;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "John Doe", "john.doe@example.com");
        updatedUserDto = new UserDto(1L, "John Doe Updated", "john.doe@example.com");
        userDtoForUpdate = new UserDtoForUpdate(1L, "John Doe Updated", "john.doe@example.com");
    }

    @Test
    void testGetAll() throws Exception {
        List<UserDto> users = Arrays.asList(userDto);
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));

        verify(userService, times(1)).getAll();
    }

    @Test
    void testGetById() throws Exception {
        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).getById(1L);
    }

    @Test
    void testAdd() throws Exception {
        when(userService.add(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).add(any(UserDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(userService.update(any(UserDtoForUpdate.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe Updated\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedUserDto.getEmail()));

        verify(userService, times(1)).update(any(UserDtoForUpdate.class));
    }

    @Test
    void testRemove() throws Exception {
        doNothing().when(userService).remove(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).remove(1L);
    }
}

