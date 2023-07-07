package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constantsShareitGateway.Constants.USER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(USER_ID) Integer userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Запрос пользователя {} на создание запроса вещи {}", userId, itemRequestDto);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getYourItemRequests(@RequestHeader(USER_ID) Integer userId) {
        log.info("Запрос пользователя {} всех его запросов вещей", userId);
        return itemRequestClient.getYourItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) Integer userId,
                                                     @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос пользователя {} всех запросов вещей с указание количества вывода from {} size {}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USER_ID) Integer userId,
                                                 @PathVariable Integer requestId) {
        log.info("Запрос пользователя {} запроса вещи Id: {} ", userId, requestId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
