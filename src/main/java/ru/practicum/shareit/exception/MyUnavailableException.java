package ru.practicum.shareit.exception;

public class MyUnavailableException extends RuntimeException {
    public MyUnavailableException(String message) {
        super(message);
    }
}
