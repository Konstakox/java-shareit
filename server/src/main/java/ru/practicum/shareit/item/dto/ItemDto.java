package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoGivenWithBookerId;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private BookingDtoGivenWithBookerId nextBooking;
    private BookingDtoGivenWithBookerId lastBooking;
    private List<CommentDto> comments;
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}


