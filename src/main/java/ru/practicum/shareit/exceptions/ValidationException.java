package ru.practicum.shareit.exceptions;

public class ValidationException extends NullPointerException {
    public ValidationException(String message) {
        super(message);
    }
}
