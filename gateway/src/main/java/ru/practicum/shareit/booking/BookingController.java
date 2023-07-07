package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constantsShareitGateway.Constants.USER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) Integer userId,
                                             @RequestBody @Valid BookingDtoIncoming bookingDtoIncoming) {
        log.info("Запрос пользователя {} на создание бронирования вещи {}", userId, bookingDtoIncoming.getItemId());
        return bookingClient.addBooking(userId, bookingDtoIncoming);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvOrRejectBooking(@RequestHeader(USER_ID) Integer userId,
                                                        @PathVariable @Positive Integer bookingId,
                                                        @RequestParam @NotBlank String approved) {
        log.info("Запрос пользователя {} на изменение статуса бронирования вещи {} на параметр {}",
                userId, bookingId, approved);
        return bookingClient.approvOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingOnlyBookerOrOwner(@RequestHeader(USER_ID) Integer userId,
                                                              @PathVariable @Positive Integer bookingId) {
        log.info("@Get /bookings {bookingId} getBookingOnlyBookerOrOwner Запрос бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.getBookingOnlyBookerOrOwner(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsUser(@RequestHeader(USER_ID) Integer userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("@Get /bookings getAllBookingsUser Запрос бронирований пользователем {} " +
                "бронирований со статусом {}", userId, state);
        return bookingClient.getAllBookingsUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsItemsUser(@RequestHeader(USER_ID) Integer userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("@Get /bookings/owner getAllBookingsUser Запрос бронирований пользователем {} " +
                "ВЕЩЕЙ бронированя со статусом {}", userId, state);
        return bookingClient.getAllBookingsItemsUser(userId, state, from, size);
    }
}
