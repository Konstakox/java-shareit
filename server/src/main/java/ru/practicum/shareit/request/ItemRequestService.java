package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithItem> getYourItemRequests(Integer userId);

    List<ItemRequestDtoWithItem> getAllItemRequests(Integer userId, Integer from, Integer size);

    ItemRequestDtoWithItem getItemRequest(Integer requestId, Integer userId);
}
