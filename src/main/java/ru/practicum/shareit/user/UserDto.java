package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.Email;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}

