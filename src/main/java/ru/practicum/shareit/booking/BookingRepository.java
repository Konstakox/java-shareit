package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByEndDesc(Integer userId); //all

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(Integer userId,
                                                                          LocalDateTime now, LocalDateTime now1); // current текущие

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime now); // past завершённые

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(Integer userId, LocalDateTime now); // future будущие

    List<Booking> findByBookerIdAndStatusOrderByEndDesc(Integer userId, StatusBooking statusBooking); // WAITING

    List<Booking> findByItem_Owner_OrderByStartDesc(Integer owner);

    List<Booking> findByItem_Owner_AndStartIsBeforeAndEndIsAfterOrderByStartDesc(Integer owner, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItem_OwnerAndEndIsBeforeOrderByStartDesc(Integer owner, LocalDateTime now);

    List<Booking> findByItem_OwnerAndStartIsAfterOrderByStartDesc(Integer owner, LocalDateTime now);

    List<Booking> findByItem_OwnerAndStatusOrderByStartDesc(Integer owner, StatusBooking statusBooking);

    List<Booking> findByItem_IdOrderByStartAsc(Integer itemId);

    List<Booking> findByBooker_IdAndStatusAndEndIsBefore(Integer userId, StatusBooking statusBooking, LocalDateTime now);
}
