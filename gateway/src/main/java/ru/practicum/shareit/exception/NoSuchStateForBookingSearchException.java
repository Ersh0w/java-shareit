package ru.practicum.shareit.exception;

public class NoSuchStateForBookingSearchException extends RuntimeException {
    public NoSuchStateForBookingSearchException(String message) {
        super(message);
    }
}
