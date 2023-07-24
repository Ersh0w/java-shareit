package ru.practicum.shareit.exception;

public class CommentBeforeBookingEndException extends RuntimeException {
    public CommentBeforeBookingEndException(String message) {
        super(message);
    }
}
