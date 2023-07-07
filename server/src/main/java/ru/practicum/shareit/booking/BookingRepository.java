package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByEndDesc(Integer userId, Pageable page); //all

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(Integer userId,
                                                                          LocalDateTime now, LocalDateTime now1, Pageable page); // current текущие

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime now, Pageable page); // past завершённые

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(Integer userId, LocalDateTime now, Pageable page); // future будущие

    List<Booking> findByBookerIdAndStatusOrderByEndDesc(Integer userId, StatusBooking statusBooking, Pageable page); // WAITING

    List<Booking> findByItem_Owner_OrderByStartDesc(Integer owner, Pageable page);

    List<Booking> findByItem_Owner_AndStartIsBeforeAndEndIsAfterOrderByStartDesc(Integer owner, LocalDateTime now, LocalDateTime now1, Pageable page);

    List<Booking> findByItem_OwnerAndEndIsBeforeOrderByStartDesc(Integer owner, LocalDateTime now, Pageable page);

    List<Booking> findByItem_OwnerAndStartIsAfterOrderByStartDesc(Integer owner, LocalDateTime now, Pageable page);

    List<Booking> findByItem_OwnerAndStatusOrderByStartDesc(Integer owner, StatusBooking statusBooking, Pageable page);

    List<Booking> findByItem_IdOrderByStartAsc(Integer itemId);

    List<Booking> findByBooker_IdAndStatusAndEndIsBefore(Integer userId, StatusBooking statusBooking, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> itemOwner, StatusBooking statusBooking);
}
