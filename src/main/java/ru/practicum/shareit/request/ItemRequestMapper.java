package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDtoWithItem toItemRequestDtoWithItem(ItemRequest itemRequest) {
        return ItemRequestDtoWithItem.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())// != null ? itemRequest.getRequestor() : null)
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto){
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(itemRequestDto.getRequestor())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest){
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }
}
