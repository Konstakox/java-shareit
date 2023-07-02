package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;
import ru.practicum.shareit.exception.MyBookingNotFoundStatusException;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.exception.MyUnavailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingServiceImpl;

    private final Integer userId1 = 1;
    private final Integer userId2 = 2;
    private final Integer bookingId = 1;
    private final User user1 = User.builder()
            .name("name")
            .email("email@email.ru")
            .build();

    private final Item item = Item.builder()
            .id(1)
            .name("name")
            .description("description")
            .available(true)
            .owner(userId1)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("name")
            .description("description")
            .available(true)
            .build();

    private final BookingDtoIncoming bookingDtoIncoming = BookingDtoIncoming.builder()
            .start(LocalDateTime.now().plusSeconds(20))
            .end(LocalDateTime.now().plusMinutes(2))
            .itemId(1)
            .build();

    private final Booking booking = Booking.builder()
            .id(bookingId)
            .status(StatusBooking.WAITING)
            .start(bookingDtoIncoming.getStart())
            .end(bookingDtoIncoming.getEnd())
            .item(item)
            .booker(user1)
            .build();

    @Test
    void addBooking_add_returnBooking() {
        item.setOwner(2);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertEquals((BookingMapper.toBookingDtoGiven(booking)),
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
    }

    @Test
    void addBooking_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenItemNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenStartAfterEnd_thenMyUnavailableExceptionThrown() {
        bookingDtoIncoming.setStart(LocalDateTime.now().plusMinutes(2));
        bookingDtoIncoming.setEnd(LocalDateTime.now());

        assertThrows(MyUnavailableException.class, () ->
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenStartEqualsEnd_thenMyUnavailableExceptionThrown() {
        bookingDtoIncoming.setStart(LocalDateTime.now());
        bookingDtoIncoming.setEnd(bookingDtoIncoming.getStart());

        assertThrows(MyUnavailableException.class, () ->
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenItemAvailableFalse_thenMyUnavailableExceptionThrown() {
        item.setAvailable(false);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        assertThrows(MyUnavailableException.class, () ->
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenUserEqualsOwner_thenMyNotFoundExceptionThrown() {
        item.setOwner(userId1);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.addBooking(userId1, bookingDtoIncoming));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvOrRejectBooking_whenValueTrue_returnBookingStatusApproved() {
        Booking bookingStatusApproved = Booking.builder()
                .id(booking.getId())
                .status(StatusBooking.APPROVED)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();

        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertEquals((BookingMapper.toBookingDtoGiven(bookingStatusApproved)),
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void approvOrRejectBooking_whenValueFalse_returnBookingStatusRejected() {
        Booking bookingStatusApproved = Booking.builder()
                .id(booking.getId())
                .status(StatusBooking.REJECTED)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();

        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertEquals((BookingMapper.toBookingDtoGiven(bookingStatusApproved)),
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "false"));

        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void approvOrRejectBooking_whenStatusTextIncorrect_thenMyUnavailableExceptionThrown() {
        String textIncorrect = "tru";
        assertThrows(MyUnavailableException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, textIncorrect));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvOrRejectBooking_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvOrRejectBooking_bookingNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvOrRejectBooking_bookingStatusApproved_thenMyBookingNotFoundStatusExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        booking.setStatus(StatusBooking.APPROVED);

        assertThrows(MyBookingNotFoundStatusException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvOrRejectBooking_itemNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvOrRejectBooking_userNotOwner_thenMyNotFoundExceptionThrown() {
        item.setOwner(2);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingOnlyBookerOrOwner_getOwner_returnBooking() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertEquals((BookingMapper.toBookingDtoGiven(booking)),
                bookingServiceImpl.getBookingOnlyBookerOrOwner(userId1, bookingId));
    }

    @Test
    void getBookingOnlyBookerOrOwner_getBooker_returnBooking() {
        item.setOwner(2);
        user1.setId(1);
        booking.setBooker(user1);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertEquals((BookingMapper.toBookingDtoGiven(booking)),
                bookingServiceImpl.getBookingOnlyBookerOrOwner(userId1, bookingId));
    }

    @Test
    void getBookingOnlyBookerOrOwner_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.getBookingOnlyBookerOrOwner(userId1, bookingId));
    }

    @Test
    void getBookingOnlyBookerOrOwner_whenBookingNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () ->
                bookingServiceImpl.approvOrRejectBooking(userId1, bookingId, "true"));
    }

    @Test
    void getAllBookingsUser_StateAll_returnBookingCollections() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByBookerIdOrderByEndDesc(eq(userId1), any()))
                .thenReturn(List.of(booking));

        String state = "All";

        assertEquals((Stream.of(booking)
                        .map(BookingMapper::toBookingDtoGiven)
                        .collect(Collectors.toList())),
                bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10));
    }

    @Test
    void getAllBookingsUser_StateCURRENT_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "CURRENT";

        bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsUser_StatePAST_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "PAST";

        bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBeforeOrderByEndDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsUser_StateFUTURE_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "FUTURE";

        bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterOrderByEndDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsUser_StateWAITING_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "WAITING";

        bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByEndDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsUser_StateREJECTED_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "REJECTED";

        bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByEndDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsUser_StateIncorrect_thenMyBookingNotFoundStatusExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "Incorrect";

        assertThrows(MyBookingNotFoundStatusException.class, () ->
                bookingServiceImpl.getAllBookingsUser(userId1, state, 0, 10));
    }

    @Test
    void getAllBookingsItemsUser_StateAll_returnBookingCollections() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItem_Owner_OrderByStartDesc(eq(userId1), any()))
                .thenReturn(List.of(booking));

        String state = "All";

        assertEquals((Stream.of(booking)
                        .map(BookingMapper::toBookingDtoGiven)
                        .collect(Collectors.toList())),
                bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10));
    }

    @Test
    void getAllBookingsItemsUser_StateCURRENT_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "CURRENT";

        bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByItem_Owner_AndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsItemsUser_StatePAST_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "PAST";

        bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByItem_OwnerAndEndIsBeforeOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsItemsUser_StateFUTURE_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "FUTURE";

        bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByItem_OwnerAndStartIsAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsItemsUser_StateWAITING_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "WAITING";

        bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByItem_OwnerAndStatusOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsItemsUser_StateREJECTED_requestHasBeenCompleted() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "REJECTED";

        bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10);

        verify(bookingRepository, times(1))
                .findByItem_OwnerAndStatusOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getAllBookingsItemsUser_StateIncorrect_thenMyBookingNotFoundStatusExceptionThrown() {
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        String state = "Incorrect";

        assertThrows(MyBookingNotFoundStatusException.class, () ->
                bookingServiceImpl.getAllBookingsItemsUser(userId1, state, 0, 10));
    }
}