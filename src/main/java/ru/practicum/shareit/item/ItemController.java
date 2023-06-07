package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MarkerItemDto;

import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.constantsShareit.Constants.Headers.USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) Integer userId,
                           @Validated(MarkerItemDto.OnCreate.class)
                           @RequestBody ItemDto itemDto) {
        log.info("Запрос пользователя {} на создание вещи {}", userId, itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable @Positive Integer itemId) {
        log.info("Запрос вещи по id {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemOwner(@RequestHeader(USER_ID) Integer userId) {
        log.info("Запрос пользователя {} всех его вещей", userId);
        return itemService.getItemOwner(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) Integer userId,
                              @RequestBody @Validated(MarkerItemDto.OnUpdate.class) ItemDto itemDto,
                              @PathVariable @Positive Integer itemId) {
        log.info("Запрос пользователя {} на редактирование вещи с id {}, изменение {}", userId, itemId, itemDto);

        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        log.info("Поиск вещи по тексту {}", text);

        return itemService.searchItems(text);
    }
}
