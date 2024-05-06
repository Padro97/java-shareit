package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import javax.validation.ValidationException;
import ru.practicum.shareit.user.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private HashMap<Long, UserDto> users = new HashMap<>();
    private HashMap<Long, String> emails = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public UserDto add(UserDto user) {
        if (emails.containsValue(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        if (user.getEmail() == null || user.getName() == null) {
            throw new ValidationException("Имя и имейл юзера не должны быть null!");
        }
        idCounter++;
        user.setId(idCounter);
        users.put(idCounter, user);

        emails.put(idCounter, user.getEmail());

        return user;
    }

    @Override
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        if (users.containsKey(userId)) {
            UserDto existingUser = users.get(userId);
            if (userDto.getName() != null) {
                existingUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                if (!userDto.getEmail().equals(existingUser.getEmail()) && emails.containsValue(userDto.getEmail())) {
                    throw new IllegalArgumentException("Пользователь с таким email уже существует");
                }
                existingUser.setEmail(userDto.getEmail());
                emails.put(userId, userDto.getEmail());
            }
            return existingUser;
        } else {
            throw new NoSuchElementException("Юзера с таким ID не существует");
        }
    }


    @Override
    public void remove(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            emails.remove(id);
        } else {
            throw new NoSuchElementException("Юзера с таким ID не существует");
        }
    }

    @Override
    public List<UserDto> getAll() {
        return new ArrayList<>(users.values());
    }


    @Override
    public UserDto getById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NoSuchElementException("Юзера с таким ID не существует");
        }
    }


}
