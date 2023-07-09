package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CommentDto {
    @Null(groups = MarkerCommentDto.OnCreate.class, message = "Ид пока не нужен")
    private Integer id;

    @NotBlank(groups = MarkerCommentDto.OnCreate.class, message = "Нет описания")
    @Size(groups = MarkerCommentDto.OnCreate.class, min = 2, max = 511, message = "Некорректное значение символов")
    private String text;

    @NotNull(message = "Должна быть вещь")
    private Item item;

    @Null(groups = MarkerCommentDto.OnCreate.class, message = "Нет автора")
    private String authorName;

    @Null(groups = MarkerCommentDto.OnCreate.class, message = "Незафиксированно время")
    private LocalDateTime created;
}
