package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item getItem(Integer itemId);

    List<Item> getItemOwner(Integer userId);

    Item updateItem(Item item);

    List<Item> searchItems(String text);
}
