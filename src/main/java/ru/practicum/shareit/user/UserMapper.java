package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userDto2User(UserDto userDto);

    UserDto user2UserDto(User user);

}
