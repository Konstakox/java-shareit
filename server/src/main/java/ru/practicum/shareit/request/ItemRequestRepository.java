package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequestor(User user);

    List<ItemRequest> findAllByRequestorIsNotOrderByCreatedDesc(User user, Pageable page);
}
