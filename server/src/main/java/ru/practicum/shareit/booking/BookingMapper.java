package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoGiven;
import ru.practicum.shareit.booking.dto.BookingDtoGivenWithBookerId;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(User user, Item item, BookingDtoIncoming bookingDtoIncoming) {
        return Booking.builder()
                .start(bookingDtoIncoming.getStart())
                .end(bookingDtoIncoming.getEnd())
                .status(bookingDtoIncoming.getStatus())
                .booker(user)
                .item(item)
                .build();
    }

    public BookingDtoGiven toBookingDtoGiven(Booking booking) {
        return BookingDtoGiven.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public BookingDtoGivenWithBookerId toBookingDtoGivenWithBookerId(Booking booking) {
        return BookingDtoGivenWithBookerId.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
}
