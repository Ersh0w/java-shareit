package ru.practicum.shareit.exception;

public class BookingNotBelongException extends RuntimeException {
    public BookingNotBelongException(String message) {
        super(message);
    }
}
