package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddUser() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.add(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDto.getName());
        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    public void testUpdateUser() {
        Long userId = 1L;
        UserDtoForUpdate userDtoForUpdate = UserDtoForUpdate.builder()
                .id(userId)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(userDtoForUpdate);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDtoForUpdate.getName());
        assertThat(result.getEmail()).isEqualTo(userDtoForUpdate.getEmail());
    }

    @Test
    public void testUpdateUserThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        UserDtoForUpdate userDtoForUpdate = UserDtoForUpdate.builder()
                .id(userId)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userDtoForUpdate))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Юзера с таким ID не существует");
    }

    @Test
    public void testRemoveUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.remove(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testGetAllUsers() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.getAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo(user.getName());
        assertThat(result.get(0).getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testGetUserByIdThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Юзера с таким ID не существует");
    }
}
