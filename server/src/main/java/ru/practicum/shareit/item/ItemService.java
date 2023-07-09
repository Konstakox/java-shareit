package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto getItem(Integer userId, Integer itemId);

    List<ItemDto> getItemOwner(Integer userId, Integer from, Integer size);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
