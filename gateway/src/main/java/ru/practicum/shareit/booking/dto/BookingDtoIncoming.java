package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoIncoming {

    @NotBlank(message = "Должно быть время начала бронирования")
    @FutureOrPresent(message = "Невозможно забронировать вещь в прошлом.")
    private LocalDateTime start;

    @NotBlank(message = "Должно быть время конеца бронирования")
    @Future(message = "Время окончания бронирования должно быть после его начала")
    private LocalDateTime end;

    @NotNull(message = "Должна быть указана вещь")
    private Integer itemId;

    private StatusBooking status;
}
