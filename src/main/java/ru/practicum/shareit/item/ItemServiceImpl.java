package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        userService.getUser(userId);
        Item newItem = ItemMapper.toItem(userId, itemDto);
        newItem = itemRepository.addItem(newItem);
        itemDto = ItemMapper.toItemDto(newItem);

        return itemDto;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemRepository.getItem(itemId);
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с ID %s не найдена", itemId));
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemOwner(Integer userId) {
        List<Item> itemOwner = itemRepository.getItemOwner(userId);

        return itemOwner.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemRepository.getItem(itemId);
        if (!userId.equals(item.getOwner())) {
            throw new NotFoundException("У вас нет вещи с ID:" + itemId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = itemRepository.updateItem(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> findItems = itemRepository.searchItems(text);
        return findItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
