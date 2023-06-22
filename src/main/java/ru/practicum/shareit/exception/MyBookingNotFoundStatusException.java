package ru.practicum.shareit.exception;

public class MyBookingNotFoundStatusException extends RuntimeException {
    public MyBookingNotFoundStatusException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}
