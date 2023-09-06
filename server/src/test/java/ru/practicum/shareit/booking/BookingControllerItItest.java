package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerItItest {
    private static final String URL = "/bookings";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    BookingDto bookingDtoIn;
    SavedBookingDto savedBookingDtoOut;
    long userId;
    boolean isApproved;

    @BeforeEach
    void setUp() {
        bookingDtoIn = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(7))
                .end(LocalDateTime.now().plusDays(10))
                .itemId(1)
                .build();
        savedBookingDtoOut = SavedBookingDto.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(7))
                .end(LocalDateTime.now().plusDays(10))
                .build();

        userId = 1;
        isApproved = true;
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(bookingService.getBookingById(savedBookingDtoOut.getId(), userId)).thenReturn(savedBookingDtoOut);

        mvc.perform(get(URL + "/" + savedBookingDtoOut.getId())
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(savedBookingDtoOut)));

        verify(bookingService).getBookingById(savedBookingDtoOut.getId(), userId);
    }

    @SneakyThrows
    @Test
    void getBookingsOfUser() {
        when(bookingService.getBookingsOfUser(0, 20, "ALL", userId))
                .thenReturn(List.of(savedBookingDtoOut));

        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(savedBookingDtoOut))));

        verify(bookingService).getBookingsOfUser(0, 20, "ALL", userId);
    }

    @SneakyThrows
    @Test
    void getBookingsOfItemsOwner() {
        when(bookingService.getBookingsOfItemsOwner(0, 20, "ALL", userId))
                .thenReturn(List.of(savedBookingDtoOut));

        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(savedBookingDtoOut))));

        verify(bookingService).getBookingsOfItemsOwner(0, 20, "ALL", userId);
    }

    @SneakyThrows
    @Test
    void saveNewBooking() {
        when(bookingService.saveNewBooking(bookingDtoIn, userId))
                .thenReturn(savedBookingDtoOut);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(savedBookingDtoOut)));

        verify(bookingService).saveNewBooking(bookingDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        when(bookingService.updateBooking(savedBookingDtoOut.getId(), userId, isApproved))
                .thenReturn(savedBookingDtoOut);

        mvc.perform(patch(URL + "/" + savedBookingDtoOut.getId())
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(savedBookingDtoOut)));

        verify(bookingService).updateBooking(savedBookingDtoOut.getId(), userId, isApproved);
    }
}
