package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public SavedBookingDto getBookingById(@PathVariable long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на получение бронирования с id " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<SavedBookingDto> getBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                   @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на получение бронирований со статусом " + state + " пользователя с id " + userId);
        return bookingService.getBookingsOfUser(from, size, state, userId);
    }

    @GetMapping("owner")
    public List<SavedBookingDto> getBookingsOfItemsOwner(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                         @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на получение бронирований от хозяина вещей со статусом " + state +
                " пользователя с id " + userId);
        return bookingService.getBookingsOfItemsOwner(from, size, state, userId);
    }

    @PostMapping
    public SavedBookingDto saveNewBooking(@RequestBody @Valid BookingDto bookingDto,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление бронирования от пользователя id " + userId);
        return bookingService.saveNewBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public SavedBookingDto updateBooking(@PathVariable long bookingId,
                                         @RequestParam(required = true) boolean approved,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на изменение статуса бронирования с id " + bookingId + ", от пользователя с id " + userId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }
}