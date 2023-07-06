package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final PageRequest page = PageRequest.of(0 / 10, 10);

    @BeforeEach
    public void addBase() {
        User user1 = userRepository.save(User.builder()
                .name("name1")
                .email("email@email.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("1name2")
                .email("2email@email.ru")
                .build());

        ItemRequest itemRequest1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужны часы")
                .requestor(user1)
                .build());
        ItemRequest itemRequest2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужен будильник")
                .requestor(user1)
                .build());
        ItemRequest itemRequest3 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужно хорошее настроение")
                .requestor(user2)
                .build());

        itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(user1.getId())
                .request(itemRequest1)
                .build());
        itemRepository.save(Item.builder()
                .name("nTwo")
                .description("TesTing description")
                .available(true)
                .owner(user1.getId())
                .request(itemRequest1)
                .build());
        itemRepository.save(Item.builder()
                .name("name3")
                .description("tesla descrip")
                .available(true)
                .owner(user2.getId())
                .request(itemRequest3)
                .build());
        itemRepository.save(Item.builder()
                .name("никому не нужная вещь")
                .description("отсутствует")
                .available(true)
                .owner(user2.getId())
                .request(itemRequest2)
                .build());
    }

    @Test
    void searchItems() {
        List<Item> items = itemRepository.searchItems("TESt", page);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals("nTwo", items.get(0).getName());
    }

    @Test
    void findAllItemByOwnerOrderById() {
        List<Item> result = itemRepository.findAllItemByOwnerOrderById(1, page);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("name", result.get(0).getName());
        assertEquals("nTwo", result.get(1).getName());
    }

    @Test
    void findByRequestIn() {
        User user1 = userRepository.findById(1)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + 1));

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor(user1);
        List<Item> result = itemRepository.findByRequestIn(itemRequests);

        assertEquals(3, result.size());
        assertEquals("name", result.get(0).getName());
        assertEquals("nTwo", result.get(1).getName());
        assertEquals("никому не нужная вещь", result.get(2).getName());
    }

    @Test
    void findAllByRequest() {
        ItemRequest itemRequest = itemRequestRepository.findById(1)
                .orElseThrow(() -> new MyNotFoundException("не найден ID: " + 1));

        List<Item> result = itemRepository.findAllByRequest(itemRequest);

        assertEquals(2, result.size());
        assertEquals("name", result.get(0).getName());
        assertEquals("nTwo", result.get(1).getName());
    }

    @AfterEach
    public void deleteItem() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}