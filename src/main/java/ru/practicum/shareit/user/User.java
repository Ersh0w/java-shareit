package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class User {
    private Integer id;
    @NotEmpty
    @NotBlank
    private String name;
    @Email
    @NotBlank
    @NotEmpty
    private String email;
}
