package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoGivenWithBookerId {
    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;

    private Integer bookerId;

    private StatusBooking status;
}
