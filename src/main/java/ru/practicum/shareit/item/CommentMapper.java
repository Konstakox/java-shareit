package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
//@RequiredArgsConstructor
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(User user, Item item, CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(commentDto.getCreated())
                .build();
    }
}
