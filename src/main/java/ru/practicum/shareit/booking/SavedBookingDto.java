package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import java.time.LocalDateTime;

@Data
@Builder
public class SavedBookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemBookingDto item;
    private UserBookingDto booker;
    private BookingStatus status;
}
