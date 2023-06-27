package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoGiven;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;

import java.util.List;

public interface BookingService {
    BookingDtoGiven addBooking(Integer userId, BookingDtoIncoming bookingDtoIncoming);

    BookingDtoGiven approvOrRejectBooking(Integer userId, Integer bookingId, String approved);

    BookingDtoGiven getBookingOnlyBookerOrOwner(Integer userId, Integer bookingId);

    List<BookingDtoGiven> getAllBookingsUser(Integer userId, String state, Integer from, Integer size);

    List<BookingDtoGiven> getAllBookingsItemsUser(Integer userId, String state, Integer from, Integer size);
}
