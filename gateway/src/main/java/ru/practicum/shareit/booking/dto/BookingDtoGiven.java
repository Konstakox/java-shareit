package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoGiven {
    @NotNull(message = "Необходимо указать id")
    private Integer id;

    @NotBlank(message = "Должно быть время начала бронирования")
    @FutureOrPresent(message = "Невозможно забронировать вещь в прошлом.")
    private LocalDateTime start;

    @NotBlank(message = "Должно быть время конеца бронирования")
    @Future(message = "Время окончания бронирования должно быть после его начала")
    private LocalDateTime end;

    @NotNull(message = "Должна быть указана вещь")
    private ItemDto item;

    @NotNull(message = "Должен быть указан бронирующий")
    private UserDto booker;

    @NotNull(message = "Не указан статус бронирования")
    private StatusBooking status;
}
