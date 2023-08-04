package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ItemDto {
    private long id;

    @NotEmpty
    @NotBlank
    private String name;

    @NotEmpty
    @NotBlank
    private String description;

    @NotNull
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
