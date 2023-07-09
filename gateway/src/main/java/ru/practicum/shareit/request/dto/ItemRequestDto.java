package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;

    @NotBlank(message = "Нет описания")
    private String description;
    private User requestor;
    @FutureOrPresent(message = "Не правильное время")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;
}
