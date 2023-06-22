package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CommentDto {
    @Null(groups = MarkerCommentDto.OnCreate.class)
    private Integer id;

    @NotBlank(groups = MarkerCommentDto.OnCreate.class)
    @Size(groups = MarkerCommentDto.OnCreate.class, min = 2, max = 511)
    private String text;

    @NotNull
    private Item item;

    @Null(groups = MarkerCommentDto.OnCreate.class)
    private String authorName;

    @Null(groups = MarkerCommentDto.OnCreate.class)
    private LocalDateTime created;
}
