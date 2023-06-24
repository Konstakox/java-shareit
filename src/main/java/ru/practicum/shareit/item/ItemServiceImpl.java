package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDtoGivenWithBookerId;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.exception.MyUnavailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        userService.getUser(userId);
        Item newItem = ItemMapper.toItem(userId, itemDto);
        newItem = itemRepository.save(newItem);
        itemDto = ItemMapper.toItemDto(newItem);
        log.info("Успешное создание вещи {}", itemDto);

        return itemDto;
    }

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MyNotFoundException("Вещь не найдена ID" + itemId));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (userId.equals(item.getOwner())) {
            itemDto.setNextBooking(getNextBooking(itemId));
            itemDto.setLastBooking(getLastBooking(itemId));
        }

        itemDto.setComments(getComments(itemId) != null ? getComments(itemId) : null);

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemOwner(Integer userId) {

        List<Item> itemOwner = itemRepository.findAllItemByOwner(userId);

        List<Booking> bookingItemOwner = bookingRepository.findAllByItemInAndStatusOrderByStart(itemOwner, StatusBooking.APPROVED);

        return itemOwner.stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setNextBooking(getNextBookingOwner(itemDto.getId(), bookingItemOwner)))
                .peek(itemDto -> itemDto.setLastBooking(getLastBookingOwner(itemDto.getId(), bookingItemOwner)))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemRepository.getReferenceById(itemId);
        if (!userId.equals(item.getOwner())) {
            log.info("У пользователя с id {} не найдена вещь с id {} ", userId, itemDto);
            throw new MyNotFoundException("У вас нет вещи с ID:" + itemId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = itemRepository.save(item);
        log.info("Вещь с id {} изменена", itemId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> findItems = itemRepository.searchItems(text);
        log.info("Вещь по тексту {} найдена", text);

        return findItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        log.info("Сервис addComment userId {}, itemId {}, commentDto {}", userId, itemId, commentDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));
        log.info("Пользователь найден ID {}", userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MyNotFoundException("Вещь не найдена ID" + itemId));
        log.info("Вещь найден ID {}", itemId);

        List<Booking> booking = bookingRepository.findByBooker_IdAndStatusAndEndIsBefore(userId, StatusBooking.APPROVED,
                LocalDateTime.now());
        log.info("Проверка пользователя что он бронировал вещь {}. Список его бронирований {}", itemId, booking);

        if (booking.isEmpty()) {
            log.info("Пользователь {} не брал в аренду вещь {}", userId, itemId);
            throw new MyUnavailableException("Пользователь " + userId + " не брал в аренду вещь" + itemId);
        }

        commentDto.setCreated(LocalDateTime.now());
        log.info("Пользователь и вещь прошли все проверки. В commentDto установлено время создания {}",
                commentDto.getCreated());
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        log.info("Сформирован Comment для сохранения в бд {}", comment);
        Comment commentNew = commentRepository.save(comment);
        log.info("Вернулся Comment из бд с присвоеным ID {}", commentNew);
        CommentDto reComment = CommentMapper.toCommentDto(commentNew);
        log.info("Подготовлен CommentDto для возврата {}", reComment);
        return reComment;
    }

    private BookingDtoGivenWithBookerId getNextBooking(Integer itemId) {
        return bookingRepository.findByItem_IdOrderByStartAsc(itemId)
                .stream()
                .filter(booking -> booking.getStatus().equals(StatusBooking.APPROVED))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingDtoGivenWithBookerId)
                .orElse(null);
    }

    private BookingDtoGivenWithBookerId getNextBookingOwner(Integer itemDtoId, List<Booking> bookingItemOwner) {
        return bookingItemOwner
                .stream()
                .filter(booking -> booking.getItem().getId().equals(itemDtoId))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingDtoGivenWithBookerId)
                .orElse(null);
    }

    private BookingDtoGivenWithBookerId getLastBooking(Integer itemId) {
        return bookingRepository.findByItem_IdOrderByStartAsc(itemId)
                .stream()
                .filter(booking -> booking.getStatus().equals(StatusBooking.APPROVED))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .map(BookingMapper::toBookingDtoGivenWithBookerId)
                .orElse(null);
    }

    private BookingDtoGivenWithBookerId getLastBookingOwner(Integer itemDtoId, List<Booking> bookingItemOwner) {
        return bookingItemOwner
                .stream()
                .filter(booking -> booking.getItem().getId().equals(itemDtoId))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .map(BookingMapper::toBookingDtoGivenWithBookerId)
                .orElse(null);
    }

    private List<CommentDto> getComments(Integer itemId) {
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
