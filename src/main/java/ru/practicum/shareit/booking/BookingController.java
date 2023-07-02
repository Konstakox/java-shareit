package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoGiven;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.constantsShareit.Constants.USER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoGiven addBooking(@RequestHeader(USER_ID) Integer userId,
                                      @RequestBody @Validated BookingDtoIncoming bookingDtoIncoming) {
        log.info("Запрос пользователя {} на создание бронирования вещи {}", userId, bookingDtoIncoming.getItemId());
        return bookingService.addBooking(userId, bookingDtoIncoming);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoGiven approvOrRejectBooking(@RequestHeader(USER_ID) Integer userId,
                                                 @PathVariable @Positive Integer bookingId,
                                                 @RequestParam @NotBlank String approved) {
        log.info("Запрос пользователя {} на изменение статуса бронирования вещи {} на параметр {}",
                userId, bookingId, approved);
        return bookingService.approvOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoGiven getBookingOnlyBookerOrOwner(@RequestHeader(USER_ID) Integer userId,
                                                       @PathVariable @Positive Integer bookingId) {
        log.info("@Get /bookings {bookingId} getBookingOnlyBookerOrOwner Запрос бронирования {} пользователем {}", bookingId, userId);
        return bookingService.getBookingOnlyBookerOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoGiven> getAllBookingsUser(@RequestHeader(USER_ID) Integer userId,
                                                    @RequestParam(required = false, defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("@Get /bookings getAllBookingsUser Запрос бронирований пользователем {} " +
                "бронирований со статусом {}", userId, state);
        return bookingService.getAllBookingsUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoGiven> getAllBookingsItemsUser(@RequestHeader(USER_ID) Integer userId,
                                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("@Get /bookings/owner getAllBookingsUser Запрос бронирований пользователем {} " +
                "ВЕЩЕЙ бронированя со статусом {}", userId, state);
        return bookingService.getAllBookingsItemsUser(userId, state, from, size);
    }
}
