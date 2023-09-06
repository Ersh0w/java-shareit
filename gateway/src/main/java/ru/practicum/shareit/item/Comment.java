package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private long id;

    @NotEmpty
    @NotNull
    private String text;

    @ToString.Exclude
    private User author;
    private LocalDateTime created;
}
