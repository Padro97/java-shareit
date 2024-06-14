package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTests {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUpdate() {
        UserDto userDto = new UserDto(null, "Initial User", "initialuser@example.com");
        UserDto savedUser = userService.add(userDto);

        UserDtoForUpdate updateUser = new UserDtoForUpdate(savedUser.getId(), "Updated User", "updateduser@example.com");

        UserDto updatedUser = userService.update(updateUser);

        assertNotNull(updatedUser);
        assertEquals(updateUser.getId(), updatedUser.getId());
        assertEquals(updateUser.getName(), updatedUser.getName());
        assertEquals(updateUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testUpdate_UserNotFound() {
        UserDtoForUpdate updateUser = new UserDtoForUpdate(999L, "Non-existent User", "nonexistent@example.com");

        assertThrows(NoSuchElementException.class, () -> {
            userService.update(updateUser);
        });
    }
}
