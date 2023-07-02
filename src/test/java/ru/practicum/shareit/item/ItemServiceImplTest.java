package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemServiceimpl;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private final Integer userId1 = 1;

    private final User user1 = User.builder()
            .name("name")
            .email("email@email.ru")
            .build();

    private final User author = User.builder()
            .name("name")
            .email("email@email.ru")
            .build();
    private final Item item = Item.builder()
            .id(1)
            .name("name")
            .description("description")
            .available(true)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();

    private final Comment comment = Comment.builder()
            .text("text")
            .item(item)
            .author(author)
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .text("text")
            .item(item)
            .build();

    private final ItemRequest itemRequest = ItemRequest.builder().id(1).build();

    private final Booking bookingLast = Booking.builder().id(100)
            .status(StatusBooking.APPROVED)
            .start(LocalDateTime.of(2000, 1, 1, 1, 1, 1))
            .end(LocalDateTime.of(2001, 2, 2, 2, 2, 2))
            .item(item)
            .booker(user1)
            .build();
    private final Booking bookingNext = Booking.builder().id(300)
            .status(StatusBooking.APPROVED)
            .start(LocalDateTime.of(3000, 1, 1, 1, 1, 1))
            .end(LocalDateTime.of(3001, 2, 2, 2, 2, 2))
            .item(item)
            .booker(user1)
            .build();

    @Test
    void addItem_whenUserFound_thenSaveItem() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.of(user1));

        when(itemRepository.save(any())).thenReturn(item);

        assertEquals((ItemMapper.toItemDto(item)),
                itemServiceimpl.addItem(userId1, (ItemMapper.toItemDto(item))));
    }

    @Test
    void addItem_whenUserFoundWithRequest_thenSaveItem() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        item.setRequest(itemRequest);

        when(itemRepository.save(any())).thenReturn(item);

        assertEquals((ItemMapper.toItemDto(item)),
                itemServiceimpl.addItem(userId1, (ItemMapper.toItemDto(item))));
    }

    @Test
    void addItem_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemServiceimpl.addItem(userId1, (ItemMapper.toItemDto(item))));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItem_whenItemFound_thenReturnItem() {
        item.setOwner(userId1);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(commentRepository.findAllByItem_Id(item.getId())).thenReturn(List.of(comment));

        when(bookingRepository.findByItem_IdOrderByStartAsc(item.getId()))
                .thenReturn((List.of(bookingNext)), (List.of(bookingLast)))
                .thenThrow(IllegalArgumentException.class);

        ItemDto expected = ItemMapper.toItemDto(item);

        expected.setComments(Stream.of(comment)
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        expected.setNextBooking(BookingMapper.toBookingDtoGivenWithBookerId(bookingNext));
        expected.setLastBooking(BookingMapper.toBookingDtoGivenWithBookerId(bookingLast));

        assertEquals(expected,
                itemServiceimpl.getItem(userId1, item.getId()));
    }

    @Test
    void getItem_whenItemNotFound_thenMyNotFoundExceptionThrown() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemServiceimpl.getItem(userId1, item.getId()));
    }

    @Test
    void getItemOwner_thenReturnItem() {
        when(itemRepository.findAllItemByOwnerOrderById(any(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of(bookingNext, bookingLast));

        when(commentRepository.findAllByItemInOrderByCreatedDesc(any())).thenReturn(List.of(comment));

        List<ItemDto> expected = Stream.of(item)
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setNextBooking(BookingMapper.toBookingDtoGivenWithBookerId(bookingNext)))
                .peek(itemDto -> itemDto.setLastBooking(BookingMapper.toBookingDtoGivenWithBookerId(bookingLast)))
                .peek(itemDto -> itemDto.setComments(List.of(CommentMapper.toCommentDto(comment))))
                .collect(Collectors.toList());

        assertEquals(expected,
                itemServiceimpl.getItemOwner(userId1, 0, 10));
    }

    @Test
    void updateItem_thenReturnItemUpdate() {
        item.setOwner(userId1);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ItemDto expected = ItemDto.builder()
                .id(1)
                .name("update")
                .description("update")
                .available(false)
                .build();
        when(itemRepository.save(item)).thenReturn(item);

        assertEquals(expected,
                itemServiceimpl.updateItem(userId1, 1, expected));
    }

    @Test
    void updateItem_whenItemNotFound_thenMyNotFoundExceptionThrown() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemServiceimpl.updateItem(userId1, 1, itemDto));
    }

    @Test
    void searchItems_whenTextFound_thenReturnItem() {
        String text = "descrip";
        when(itemRepository.searchItems(eq(text), any())).thenReturn(List.of(item));

        assertEquals(Stream.of(item).map(ItemMapper::toItemDto).collect(Collectors.toList()),
                itemServiceimpl.searchItems(text, 0, 10));
    }

    @Test
    void searchItems_whenTextNull_thenReturnEmptyList() {
        String text = null;

        assertEquals((Collections.emptyList()),
                itemServiceimpl.searchItems(text, 0, 10));
    }

    @Test
    void searchItems_whenTextEmpty_thenReturnEmptyList() {
        String text = "";

        assertEquals((Collections.emptyList()),
                itemServiceimpl.searchItems(text, 0, 10));
    }

    @Test
    void addComment_thenSavedComment() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findByBooker_IdAndStatusAndEndIsBefore(eq(userId1), eq(StatusBooking.APPROVED),
                any(LocalDateTime.class))).thenReturn(List.of(bookingNext, bookingLast));

        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actual = itemServiceimpl.addComment(userId1, item.getId(), commentDto);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();

        assertEquals(commentDto.getText(), savedComment.getText());
        assertEquals(commentDto.getItem(), savedComment.getItem());
    }

    @Test
    void addComment_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemServiceimpl.addComment(userId1, item.getId(), commentDto));
    }

    @Test
    void addComment_whenItemNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                itemServiceimpl.addComment(userId1, item.getId(), commentDto));
    }
}