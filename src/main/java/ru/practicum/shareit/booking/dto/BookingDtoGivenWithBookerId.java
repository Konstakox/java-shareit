package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoGivenWithBookerId {
    @NotNull
    private Integer id;

    @NotBlank
    @FutureOrPresent
    private LocalDateTime start;

    @NotBlank
    @Future
    private LocalDateTime end;

    @NotNull
    private ItemDto item;

    @NotNull
    private Integer bookerId;

    @NotNull
    private StatusBooking status;
}
