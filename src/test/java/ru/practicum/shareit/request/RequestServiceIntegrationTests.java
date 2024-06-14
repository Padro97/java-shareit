package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestForDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RequestServiceIntegrationTests {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Test
    public void testGetById() {
        UserDto userDto = new UserDto(null, "Test User", "testuser@example.com");
        userDto = userService.add(userDto); // assuming a method to add user

        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());

        ItemRequest request = ItemRequest.builder()
                .description("Test request")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        request = requestRepository.save(request);

        ItemRequestForDto result = requestService.getById(request.getId(), userDto.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertTrue(result.getItems().isEmpty()); // Assuming no items associated with request initially
    }

    @Test
    public void testGetById_RequestNotFound() {
        UserDto userDto = new UserDto(null, "Test User", "testuser@example.com");
        userDto = userService.add(userDto);

        final Long nonExistentRequestId = 999L;
        UserDto finalUserDto = userDto;
        assertThrows(NoSuchElementException.class, () -> {
            requestService.getById(nonExistentRequestId, finalUserDto.getId());
        });
    }
}

