package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequestDtoWithItem {
    private Integer id;
    private String description;
    private User requestor;
    //    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;
    private List<ItemDto> items;
}
