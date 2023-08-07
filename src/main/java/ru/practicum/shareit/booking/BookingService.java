package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    SavedBookingDto getBookingById(long bookingId, long userId);

    List<SavedBookingDto> getBookingsOfUser(long from, long size, String state, long userId);

    List<SavedBookingDto> getBookingsOfItemsOwner(long from, long size, String state, long userId);

    SavedBookingDto saveNewBooking(BookingDto bookingDto, long userId);

    SavedBookingDto updateBooking(long bookingId, long userId, boolean isApproved);
}
