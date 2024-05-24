package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class UserDtoForUpdate {
    private Long id;

    private String name;

    @Email
    private String email;
}
