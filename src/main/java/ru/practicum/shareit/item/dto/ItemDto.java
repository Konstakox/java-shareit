package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    @Null(groups = MarkerItemDto.OnCreate.class)
    private Integer id;
    @NotBlank(groups = MarkerItemDto.OnCreate.class)
    private String name;
    @NotBlank(groups = MarkerItemDto.OnCreate.class)
    private String description;
    @NotNull(groups = MarkerItemDto.OnCreate.class)
    private Boolean available;
    private Integer request;

}
