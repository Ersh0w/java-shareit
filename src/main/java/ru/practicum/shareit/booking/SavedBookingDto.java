package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedBookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemBookingDto item;
    private UserBookingDto booker;
    private BookingStatus status;
}
