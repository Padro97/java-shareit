package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
public class User {

    @NotNull
    Long id;
    @NotBlank
    String name;
    @Email
    @NotNull
    String email;
}
