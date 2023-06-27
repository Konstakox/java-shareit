package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoGiven;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;
import ru.practicum.shareit.exception.MyBookingNotFoundStatusException;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.exception.MyUnavailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoGiven addBooking(Integer userId, BookingDtoIncoming bookingDtoIncoming) {
        log.info("Добавление нового бронирования addBooking:");
        if (bookingDtoIncoming.getStart().isAfter(bookingDtoIncoming.getEnd()) ||
                bookingDtoIncoming.getStart().equals(bookingDtoIncoming.getEnd())) {
            log.info("throw new MyUnavailableException Нельзя забронировать, время начала бронирования {} " +
                    "позже окончания бронирования {} Или совпадают", bookingDtoIncoming.getStart(), bookingDtoIncoming.getEnd());
            throw new MyUnavailableException("Нельзя забронировать, время начала бронирования "
                    + bookingDtoIncoming.getStart() + " позже окончания бронирования " + bookingDtoIncoming.getEnd()
                    + ". Или совпадают");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));

        Item item = itemRepository.findById(bookingDtoIncoming.getItemId())
                .orElseThrow(() -> new MyNotFoundException("Вещь не найдена ID " + bookingDtoIncoming.getItemId()));

        if (!item.getAvailable()) {
            log.info("Вещь недоступна для бронирования ID {} статус {}", bookingDtoIncoming.getItemId(), item.getAvailable());
            throw new MyUnavailableException("Вещь недоступна для бронирования ID " + bookingDtoIncoming.getItemId());
        }

        if (userId.equals(item.getOwner())) {
            throw new MyNotFoundException("Нельзя забронировать свою вещь");
        }

        bookingDtoIncoming.setStatus(StatusBooking.WAITING);
        log.info("Статус нового бронирования {} установлен WAITING", bookingDtoIncoming);
        Booking booking = BookingMapper.toBooking(user, item, bookingDtoIncoming);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Бронирование создано {}", newBooking);

        return BookingMapper.toBookingDtoGiven(newBooking);
    }

    @Override
    public BookingDtoGiven approvOrRejectBooking(Integer userId, Integer bookingId, String approved) {
        if (!(approved.equalsIgnoreCase("true") || approved.equalsIgnoreCase("false"))) {
            log.info("Нельзя установить статус бронирования " + approved);
            throw new MyUnavailableException("Нельзя установить статус бронирования " + approved);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));
        log.info("Пользователь найден");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new MyNotFoundException("Бронирование не найдено ID" + bookingId));
        log.info("Бронирование найдено");

        if (booking.getStatus().equals(StatusBooking.APPROVED)) {
            log.info("Нельзя изменить статус после подтверждения APPROVED ");
            throw new MyBookingNotFoundStatusException();
        }

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new MyNotFoundException("Вещь не найдена ID: "));
        log.info("Вещь найдена {} ", item);

        if (!userId.equals(booking.getItem().getOwner()) ||
                booking.getStatus().equals(StatusBooking.CANCELED) ||
                !item.getAvailable()) {
            log.info("Вещь недоступна для бронирования ID юзер не овнер или статус CANCELED" + bookingId);
            throw new MyNotFoundException("Вещь недоступна для бронирования ID" + bookingId);
        }

        if (approved.equals("true")) {
            booking.setStatus(StatusBooking.APPROVED);
            log.info("Установлен статус бронирования APPROVED");
        }
        if (approved.equals("false")) {
            booking.setStatus(StatusBooking.REJECTED);
            log.info("Установлен статус бронирования REJECTED");
        }
        booking = bookingRepository.save(booking);
        log.info("Бронирование сохранено {}", booking);

        return BookingMapper.toBookingDtoGiven(booking);
    }

    @Override
    public BookingDtoGiven getBookingOnlyBookerOrOwner(Integer userId, Integer bookingId) {
        log.info("getBookingOnlyBookerOrOwner :");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new MyNotFoundException("Бронирование не найдено ID" + bookingId));

        if (!(userId.equals(booking.getItem().getOwner()) || userId.equals(booking.getBooker().getId()))) {
            log.info("getBookingOnlyBookerOrOwner throw new MyUnavailableException Вещь ID {} " +
                    "недоступна для бронирования пользователем ID {} ", bookingId, userId);
            throw new MyNotFoundException("Вещь ID" + bookingId +
                    " недоступна для бронирования пользователем ID" + userId);
        }

        return BookingMapper.toBookingDtoGiven(booking);
    }

    @Override
    public List<BookingDtoGiven> getAllBookingsUser(Integer userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));

        List<Booking> bookings = new ArrayList<>();
        state = state.toUpperCase().trim();
        LocalDateTime now = LocalDateTime.now();

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(userId, now, now, page);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId, now, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId, now, page);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, StatusBooking.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, StatusBooking.REJECTED, page);
                break;
            default:
                throw new MyBookingNotFoundStatusException();
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoGiven)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoGiven> getAllBookingsItemsUser(Integer userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));


        List<Booking> bookings = new ArrayList<>();
        state = state.toUpperCase().trim();
        LocalDateTime now = LocalDateTime.now();

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItem_Owner_OrderByStartDesc(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_AndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now,
                        now, page);
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_OwnerAndEndIsBeforeOrderByStartDesc(userId, now, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_OwnerAndStartIsAfterOrderByStartDesc(userId, now, page);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(userId, StatusBooking.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED, page);
                break;
            default:
                throw new MyBookingNotFoundStatusException();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoGiven)
                .collect(Collectors.toList());
    }
}
