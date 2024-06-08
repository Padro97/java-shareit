package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Transactional
    @Override
    public UserDto add(UserDto user) {
        return mapper.toDto(repository.save(mapper.userDtoToUser(user)));
    }

    @Transactional
    @Override
    public UserDto update(UserDtoForUpdate user) {
        Long userId = user.getId();

        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Юзера с таким ID не существует"));

        if (user.getName() != null && !user.getName().isEmpty()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        return mapper.toDto(repository.save(existingUser));
    }

    @Transactional
    @Override
    public void remove(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return mapper.usersToUserDto(repository.findAll());
    }


    @Override
    public UserDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Юзера с таким ID не существует"));

        return mapper.toDto(user);
    }
}
