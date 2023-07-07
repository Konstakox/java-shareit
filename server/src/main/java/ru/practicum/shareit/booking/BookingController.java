package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoGiven;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;

import java.util.List;

import static ru.practicum.shareit.constantsShareitServer.Constants.USER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoGiven addBooking(@RequestHeader(USER_ID) Integer userId,
                                      @RequestBody BookingDtoIncoming bookingDtoIncoming) {
        log.info("Запрос пользователя {} на создание бронирования вещи {}", userId, bookingDtoIncoming.getItemId());
        return bookingService.addBooking(userId, bookingDtoIncoming);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoGiven approvOrRejectBooking(@RequestHeader(USER_ID) Integer userId,
                                                 @PathVariable Integer bookingId,
                                                 @RequestParam String approved) {
        log.info("Запрос пользователя {} на изменение статуса бронирования вещи {} на параметр {}",
                userId, bookingId, approved);
        return bookingService.approvOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoGiven getBookingOnlyBookerOrOwner(@RequestHeader(USER_ID) Integer userId,
                                                       @PathVariable Integer bookingId) {
        log.info("@Get /bookings {bookingId} getBookingOnlyBookerOrOwner Запрос бронирования {} пользователем {}", bookingId, userId);
        return bookingService.getBookingOnlyBookerOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoGiven> getAllBookingsUser(@RequestHeader(USER_ID) Integer userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("@Get /bookings getAllBookingsUser Запрос бронирований пользователем {} " +
                "бронирований со статусом {}", userId, state);
        return bookingService.getAllBookingsUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoGiven> getAllBookingsItemsUser(@RequestHeader(USER_ID) Integer userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("@Get /bookings/owner getAllBookingsUser Запрос бронирований пользователем {} " +
                "ВЕЩЕЙ бронированя со статусом {}", userId, state);
        return bookingService.getAllBookingsItemsUser(userId, state, from, size);
    }
}
