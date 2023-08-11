package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static SavedBookingDto toSavedBookingDto(Booking booking) {
        return SavedBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .id(booking.getId())
                .status(booking.getStatus())
                .build();
    }

    public static List<SavedBookingDto> toSavedBookingDtoList(List<Booking> bookings) {
        List<SavedBookingDto> savedBookingsDto = new ArrayList<>();

        for (Booking booking : bookings) {
            SavedBookingDto savedBooking = toSavedBookingDto(booking);
            savedBooking.setBooker(UserMapper.toUserBookingDto(booking.getBooker()));
            savedBooking.setItem(ItemMapper.toItemBookingDto(booking.getItem()));
            savedBookingsDto.add(savedBooking);
        }

        return savedBookingsDto;
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(booking.getId());
        bookingItemDto.setBookerId(booking.getBooker().getId());

        return bookingItemDto;
    }
}
