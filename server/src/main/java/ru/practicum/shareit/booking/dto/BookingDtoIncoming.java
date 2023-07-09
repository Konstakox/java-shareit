package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoIncoming {

    private LocalDateTime start;

    private LocalDateTime end;

    private Integer itemId;

    private StatusBooking status;
}
