package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, UserDto> usersByUserId = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long nextId = 0L;

    @Override
    public UserDto add(UserDto userDto) {
        log.info("Attempt to add user {}", userDto);
        String email = userDto.getEmail();
        if (!emails.contains(email)) {
            Long userId = getNextValidId();
            UserDto userWithId = userDto.toBuilder()
                    .id(userId)
                    .build();
            emails.add(email);
            usersByUserId.put(userWithId.getId(), userWithId);
            log.info("User with id={} successfully added", userWithId.getId());
            return userWithId;
        }
        throw new DuplicatedEmailException();
    }

    @Override
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        if (usersByUserId.containsKey(userId)) {
            log.info("Attempt to change user with id={}", userId);
            if (!emails.contains(userDto.getEmail())) {
                return updateUser(userDto, userId);
            } else {
                String emailFromStorage = usersByUserId.get(userId).getEmail();
                if (emailFromStorage.equals(userDto.getEmail())) {
                    return updateUser(userDto, userId);
                } else {
                    throw new DuplicatedEmailException();
                }
            }
        }
        log.warn("User with id={} is not present", userId);
        throw new UserNotFoundException(userId);
    }

    private UserDto updateUser(UserDto userDto, Long userId) {
        String email = usersByUserId.get(userId).getEmail();
        emails.remove(email);
        usersByUserId.put(userId, userDto);
        emails.add(userDto.getEmail());
        log.info("User with id={} successfully updated", userId);
        return userDto;
    }

    @Override
    public void delete(Long id) {
        if (usersByUserId.containsKey(id)) {
            log.info("Attempt to delete user with id={}", id);
            String email = usersByUserId.get(id).getEmail();
            emails.remove(email);
            usersByUserId.remove(id);
            log.info("User with id={} successfully deleted", id);
            return;
        }
        log.warn("User with id={} is not present", id);
        throw new UserNotFoundException(id);
    }

    @Override
    public Collection<UserDto> getAll() {
        return new ArrayList<>(usersByUserId.values());
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        return Optional.ofNullable(usersByUserId.get(id));
    }

    private Long getNextValidId() {
        nextId++;
        return nextId;
    }
}
