package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.List;

import static ru.practicum.shareit.constantsShareitServer.Constants.USER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(USER_ID) Integer userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос пользователя {} на создание запроса вещи {}", userId, itemRequestDto);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithItem> getYourItemRequests(@RequestHeader(USER_ID) Integer userId) {
        log.info("Запрос пользователя {} всех его запросов вещей", userId);
        return itemRequestService.getYourItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItem> getAllItemRequests(@RequestHeader(USER_ID) Integer userId,
                                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос пользователя {} всех запросов вещей с указание количества вывода from {} size {}", userId, from, size);
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItem getItemRequest(@RequestHeader(USER_ID) Integer userId,
                                                 @PathVariable Integer requestId) {
        log.info("Запрос пользователя {} запроса вещи Id: {} ", userId, requestId);
        return itemRequestService.getItemRequest(requestId, userId);
    }
}
