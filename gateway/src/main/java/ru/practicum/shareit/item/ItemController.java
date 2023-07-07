package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MarkerCommentDto;
import ru.practicum.shareit.item.dto.MarkerItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constantsShareitGateway.Constants.USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) Integer userId,
                                          @Validated(MarkerItemDto.OnCreate.class)
                                          @RequestBody ItemDto itemDto) {
        log.info("Запрос пользователя {} на создание вещи {}", userId, itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) Integer userId,
                                          @PathVariable @Positive Integer itemId) {
        log.info("Запрос вещи по id {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemOwner(@RequestHeader(USER_ID) Integer userId,
                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос пользователя {} всех его вещей", userId);
        return itemClient.getItemOwner(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Integer userId,
                                             @RequestBody @Validated(MarkerItemDto.OnUpdate.class) ItemDto itemDto,
                                             @PathVariable @Positive Integer itemId) {
        log.info("Запрос пользователя {} на редактирование вещи с id {}, изменение {}", userId, itemId, itemDto);

        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String text,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Поиск вещи по тексту {}", text);

        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@RequestHeader(USER_ID) Integer userId,
                                          @RequestBody @Validated(MarkerCommentDto.OnCreate.class) CommentDto commentDto,
                                          @PathVariable @Positive Integer itemId) {
        log.info("Запрос пользователя {} на создание коментария {} к вещи {}", userId, commentDto, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
