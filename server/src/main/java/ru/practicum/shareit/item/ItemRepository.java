package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllItemByOwnerOrderById(Integer userId, Pageable page);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true ")
    List<Item> searchItems(String text, Pageable page);

    List<Item> findByRequestIn(List<ItemRequest> itemRequests);

    List<Item> findAllByRequest(ItemRequest itemRequest);
}
