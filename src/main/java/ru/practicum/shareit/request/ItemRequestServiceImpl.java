package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        log.info("Сервис addItemRequest начал userId {} itemRequestDto {}", userId, itemRequestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));
        log.info("Пользователь найден {}", user);
        itemRequestDto.setRequestor(user);
        log.info("Добавление Requestor {}", itemRequestDto);
        itemRequestDto.setCreated(LocalDateTime.now());
        log.info("Добавление Created {}", itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        log.info("Мапинг в ItemRequest {}", itemRequest);
        itemRequest = itemRequestRepository.save(itemRequest);
        log.info("Запись в бд  {}", itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItem> getYourItemRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor(user);
        List<Item> itemsOnRequest = itemRepository.findByRequestIn(itemRequests);

        return itemRequestDtoSetItem(itemRequests, itemsOnRequest);
    }

    @Override
    public List<ItemRequestDtoWithItem> getAllItemRequests(Integer userId, Integer from, Integer size) {
        log.info("Сервис getAllItemRequests");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));
        log.info("Пользователь найден {}", user);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Пользователь PageRequest page {}", page);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIsNotOrderByCreatedDesc(user, page);
        List<Item> itemsOnRequest = itemRepository.findByRequestIn(itemRequests);

        return itemRequestDtoSetItem(itemRequests, itemsOnRequest);
    }

    private List<ItemRequestDtoWithItem> itemRequestDtoSetItem(List<ItemRequest> itemRequests, List<Item> itemsOnRequest) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDtoWithItem)
                .peek(itemRequestDtoWithItem -> itemRequestDtoWithItem.setItems(
                        itemsOnRequest.stream()
                                .filter(item -> item.getRequest().getId().equals(itemRequestDtoWithItem.getId()))
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemRequestDtoWithItem::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItem getItemRequest(Integer requestId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new MyNotFoundException("Запрос вещи не найден ID: " + requestId));

        List<Item> items = itemRepository.findAllByRequest(itemRequest);
        ItemRequestDtoWithItem itemRequestDtoWithItem = ItemRequestMapper.toItemRequestDtoWithItem(itemRequest);

        itemRequestDtoWithItem.setItems(items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));

        return itemRequestDtoWithItem;
    }
}
