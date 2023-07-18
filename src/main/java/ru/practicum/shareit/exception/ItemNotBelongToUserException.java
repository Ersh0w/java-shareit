package ru.practicum.shareit.exception;

public class ItemNotBelongToUserException extends RuntimeException {
    public ItemNotBelongToUserException(String message) {
        super(message);
    }
}
