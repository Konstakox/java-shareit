package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoGivenWithBookerId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private BookingDtoGivenWithBookerId nextBooking;
    private BookingDtoGivenWithBookerId lastBooking;
    private List<CommentDto> comments;
    @Null(groups = MarkerItemDto.OnCreate.class, message = "Ид пока не нужен")
    private Integer id;
    @NotBlank(groups = MarkerItemDto.OnCreate.class, message = "Нет имени")
    private String name;
    @NotBlank(groups = MarkerItemDto.OnCreate.class, message = "Нет описания")
    private String description;
    @NotNull(groups = MarkerItemDto.OnCreate.class, message = "Нет статуса вещи")
    private Boolean available;
    private Integer requestId;
}


