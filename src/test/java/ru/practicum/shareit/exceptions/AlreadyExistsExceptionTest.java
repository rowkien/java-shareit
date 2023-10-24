package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AlreadyExistsExceptionTest {

    @Test
    public void getMessage() {
        AlreadyExistsException alreadyExistsException = new AlreadyExistsException("message");
        Assertions.assertEquals(alreadyExistsException.getMessage(), "message");
    }

}