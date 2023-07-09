package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private final Integer userId1 = 1;
    private final User user1 = User.builder()
            .name("name")
            .email("email@email.ru")
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1)
            .description("description")
            .requestor(user1)
            .build();
    private final Item item = Item.builder()
            .id(1)
            .name("name")
            .description("description")
            .available(true)
            .owner(userId1)
            .request(itemRequest)
            .build();
    private final ItemDto itemDto = ItemMapper.toItemDto(item);
    private final ItemRequestDtoWithItem itemRequestDtoWithItem = ItemRequestDtoWithItem.builder()
            .id(1)
            .description("description")
            .requestor(user1)
            .items(List.of(itemDto))
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("description")
            .requestor(user1)
            .build();
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestServiceImpl;

    @Test
    void addItemRequest_add_returnItemRequest() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto expected = itemRequestServiceImpl.addItemRequest(userId1, itemRequestDto);

        assertThat(itemRequestDto.getDescription()).isEqualTo(expected.getDescription());
    }

    @Test
    void addItemRequest_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemRequestServiceImpl.addItemRequest(userId1, itemRequestDto));

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getYourItemRequests_returnItemRequestCollections() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findAllByRequestor(user1)).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIn(any())).thenReturn(List.of(item));

        List<Item> itemsOnRequest = List.of(item);
        List<ItemRequestDtoWithItem> itemRequests = List.of(itemRequestDtoWithItem);
        List<ItemRequestDtoWithItem> expected = itemRequests.stream()
                .peek(itemRequestDtoWithItem -> itemRequestDtoWithItem.setItems(itemsOnRequest.stream()
                        .filter(item -> item.getRequest().getId().equals(itemRequestDtoWithItem.getId()))
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemRequestDtoWithItem::getCreated))
                .collect(Collectors.toList());

        assertEquals((expected),
                itemRequestServiceImpl.getYourItemRequests(userId1));
    }

    @Test
    void getYourItemRequests_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemRequestServiceImpl.getYourItemRequests(userId1));

        verify(itemRequestRepository, never()).findAllByRequestor(any());
    }

    @Test
    void getAllItemRequests_returnItemRequestCollections() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findAllByRequestorIsNotOrderByCreatedDesc(eq(user1), any()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIn(any())).thenReturn(List.of(item));

        List<Item> itemsOnRequest = List.of(item);
        List<ItemRequestDtoWithItem> itemRequests = List.of(itemRequestDtoWithItem);
        List<ItemRequestDtoWithItem> expected = itemRequests.stream()
                .peek(itemRequestDtoWithItem -> itemRequestDtoWithItem.setItems(itemsOnRequest.stream()
                        .filter(item -> item.getRequest().getId().equals(itemRequestDtoWithItem.getId()))
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemRequestDtoWithItem::getCreated))
                .collect(Collectors.toList());

        assertEquals((expected),
                itemRequestServiceImpl.getAllItemRequests(userId1, 0, 10));
    }

    @Test
    void getAllItemRequests_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemRequestServiceImpl.getAllItemRequests(userId1, 0, 10));

        verify(itemRequestRepository, never()).findAllByRequestorIsNotOrderByCreatedDesc(any(), any());
    }

    @Test
    void getItemRequest_returnItemRequestCollections() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(List.of(item));

        ItemRequestDtoWithItem expected = ItemRequestMapper.toItemRequestDtoWithItem(itemRequest);
        expected.setItems(Stream.of(item)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));

        assertEquals((expected),
                itemRequestServiceImpl.getItemRequest(itemRequest.getId(), userId1));
    }

    @Test
    void getItemRequest_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemRequestServiceImpl.getItemRequest(itemRequest.getId(), userId1));

        verify(itemRepository, never()).findAllByRequest(itemRequest);
    }

    @Test
    void getItemRequest_whenItemRequestNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemRequestServiceImpl.getItemRequest(itemRequest.getId(), userId1));

        verify(itemRepository, never()).findAllByRequest(itemRequest);
    }
}