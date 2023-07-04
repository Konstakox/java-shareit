package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@AutoConfigureTestDatabase
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final PageRequest page = PageRequest.of(0 / 10, 10);

    @BeforeEach
    public void addBase() {
        userRepository.save(User.builder()
                .name("name1")
                .email("email@email.ru")
                .build());

        userRepository.save(User.builder()
                .name("1name2")
                .email("2email@email.ru")
                .build());

        itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(1)
                .build());

        itemRepository.save(Item.builder()
                .name("nTwo")
                .description("TesTing description")
                .available(true)
                .owner(1)
                .build());

        itemRepository.save(Item.builder()
                .name("name3")
                .description("tesla descrip")
                .available(true)
                .owner(2)
                .build());
    }

    @Test
    void searchItems() {
        List<Item> items = itemRepository.searchItems("TESt", page);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals("nTwo", items.get(0).getName());
    }

    @AfterEach
    public void deleteItem() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}