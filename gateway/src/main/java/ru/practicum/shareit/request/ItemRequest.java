package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {
    private Long id;

    @NotBlank
    @NotNull
    private String description;

    private LocalDateTime created;

    @ToString.Exclude
    private User requestor;
}