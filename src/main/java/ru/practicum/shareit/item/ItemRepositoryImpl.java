package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private int idItem = 1;

    @Override
    public Item addItem(Item item) {
        item.setId(idItem++);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item getItem(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemOwner(Integer userId) {
        return items.values().stream().filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(
                        (((Predicate<Item>) item -> item.getName().toLowerCase().contains(text.toLowerCase()))
                                .or(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())))
                                .and(Item::getAvailable))
                .collect(Collectors.toList());
    }
}
