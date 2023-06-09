package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.constantsShareitServer.Constants.USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) Integer userId,
                           @RequestBody ItemDto itemDto) {
        log.info("Запрос пользователя {} на создание вещи {}", userId, itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_ID) Integer userId,
                           @PathVariable Integer itemId) {
        log.info("Запрос вещи по id {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemOwner(@RequestHeader(USER_ID) Integer userId,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос пользователя {} всех его вещей", userId);
        return itemService.getItemOwner(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) Integer userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Integer itemId) {
        log.info("Запрос пользователя {} на редактирование вещи с id {}, изменение {}", userId, itemId, itemDto);

        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поиск вещи по тексту {}", text);

        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@RequestHeader(USER_ID) Integer userId,
                              @RequestBody CommentDto commentDto,
                              @PathVariable Integer itemId) {
        log.info("Запрос пользователя {} на создание коментария {} к вещи {}", userId, commentDto, itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
