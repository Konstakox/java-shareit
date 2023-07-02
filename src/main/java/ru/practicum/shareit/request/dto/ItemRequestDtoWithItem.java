package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequestDtoWithItem {
    private Integer id;
    private String description;
    private User requestor;
    @FutureOrPresent
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;
    private List<ItemDto> items;
}
