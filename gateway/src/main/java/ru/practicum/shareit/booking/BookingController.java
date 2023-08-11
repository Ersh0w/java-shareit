package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NoSuchStateForBookingSearchException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable("bookingId") long bookingId) {
        log.info("получен запрос на получение бронирования с id " + bookingId);

        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                    @RequestParam(defaultValue = "20") @Positive long size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new NoSuchStateForBookingSearchException("Unknown state: " + state));

        log.info("получен запрос на получение бронирований со статусом " + state + " пользователя с id " + userId);

        return bookingClient.getBookingsOfUser(from, size, stateParam, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfItemsOwner(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                          @RequestParam(defaultValue = "20") @Positive long size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new NoSuchStateForBookingSearchException("Unknown state: " + state));

        log.info("получен запрос на получение бронирований от хозяина вещей со статусом " + state +
                " пользователя с id " + userId);

        return bookingClient.getBookingsOfItemsOwner(from, size, stateParam, userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewBooking(@RequestBody @Valid BookingRequestDto bookingDto,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление бронирования от пользователя id " + userId);

        return bookingClient.saveNewBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable long bookingId,
                                                @RequestParam(required = true) boolean approved,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на изменение статуса бронирования с id " + bookingId + ", от пользователя с id " + userId);

        return bookingClient.updateBooking(bookingId, userId, approved);
    }
}