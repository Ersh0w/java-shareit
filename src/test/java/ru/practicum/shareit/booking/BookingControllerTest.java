package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void getBookingById() {
        long bookingId = 1;
        long userId = 1;
        SavedBookingDto expectedBookingDto = new SavedBookingDto();
        Mockito.when(bookingService.getBookingById(bookingId, userId)).thenReturn(expectedBookingDto);

        SavedBookingDto savedBookingDto = bookingController.getBookingById(bookingId, userId);

        assertEquals(expectedBookingDto, savedBookingDto);
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    void getBookingsOfUser() {
        String state = "ALL";
        long userId = 1;
        long from = 0;
        long size = 20;
        List<SavedBookingDto> expectedSavedBookingDtoList = List.of(new SavedBookingDto());
        Mockito.when(bookingService.getBookingsOfUser(from, size, state, userId))
                .thenReturn(expectedSavedBookingDtoList);

        List<SavedBookingDto> savedBookingDtoList = bookingController.getBookingsOfUser(state, userId, from, size);

        assertEquals(expectedSavedBookingDtoList, savedBookingDtoList);
        verify(bookingService, times(1)).getBookingsOfUser(from, size, state, userId);
    }

    @Test
    void getBookingsOfItemsOwner() {
        String state = "ALL";
        long userId = 1;
        long from = 0;
        long size = 20;
        List<SavedBookingDto> expectedSavedBookingDtoList = List.of(new SavedBookingDto());
        Mockito.when(bookingService.getBookingsOfItemsOwner(from, size, state, userId))
                .thenReturn(expectedSavedBookingDtoList);

        List<SavedBookingDto> savedBookingDtoList = bookingController
                .getBookingsOfItemsOwner(state, userId, from, size);

        assertEquals(expectedSavedBookingDtoList, savedBookingDtoList);
        verify(bookingService, times(1)).getBookingsOfItemsOwner(from, size, state, userId);
    }

    @Test
    void saveNewBooking() {
        long userId = 1;
        SavedBookingDto expectedBookingDto = new SavedBookingDto();
        BookingDto bookingDto = new BookingDto();
        Mockito.when(bookingService.saveNewBooking(bookingDto, userId)).thenReturn(expectedBookingDto);

        SavedBookingDto savedBookingDto = bookingController.saveNewBooking(bookingDto, userId);

        assertEquals(expectedBookingDto, savedBookingDto);
        verify(bookingService, times(1)).saveNewBooking(bookingDto, userId);
    }

    @Test
    void updateBooking() {
        long userId = 1;
        long bookingId = 1;
        boolean approved = true;
        SavedBookingDto expectedBookingDto = new SavedBookingDto();
        Mockito.when(bookingService.updateBooking(bookingId, userId, approved)).thenReturn(expectedBookingDto);

        SavedBookingDto savedBookingDto = bookingController.updateBooking(bookingId, approved, userId);

        assertEquals(expectedBookingDto, savedBookingDto);
        verify(bookingService, times(1)).updateBooking(bookingId, userId, approved);
    }
}