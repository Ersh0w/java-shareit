package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemBookingDto {
    private long id;
    private String name;
}
