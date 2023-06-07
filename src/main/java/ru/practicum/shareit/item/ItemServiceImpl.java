package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Успешное создание вещи {}", itemDto);

        return itemDto;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemRepository.getItem(itemId);
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с ID %s не найдена", itemId));
        }
        log.info("Вещь найдена, id {}", item.getId());

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemOwner(Integer userId) {
        List<Item> itemOwner = itemRepository.getItemOwner(userId);
        log.info("Вещи пользователя {} найдены", userId);

        return itemOwner.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemRepository.getItem(itemId);
        if (!userId.equals(item.getOwner())) {
            log.info("У пользователя с id {} не найдена вещь с id {} ", userId ,itemDto);
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
        log.info("Вещь с id {} изменена", itemId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> findItems = itemRepository.searchItems(text);
        log.info("Вещь по тексту {} найдена", text);

        return findItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
