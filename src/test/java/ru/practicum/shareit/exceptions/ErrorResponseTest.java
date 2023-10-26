package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    @Test
    void getMessage() {
        ErrorResponse response = new ErrorResponse("message");
        assertEquals("message", response.getError());
    }

}