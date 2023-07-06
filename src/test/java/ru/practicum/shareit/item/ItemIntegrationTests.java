package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemIntegrationTests {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    private final User user1 = User.builder()
            .name("name")
            .email("email@email.ru")
            .build();
    private final User author = User.builder()
            .name("nameauthor")
            .email("emailauthor@email.ru")
            .build();
    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(1)
            .build();
    private final Comment comment = Comment.builder()
            .text("textcomment")
            .item(item)
            .author(author)
            .build();
    private final Booking bookingLast = Booking.builder()
            .status(StatusBooking.APPROVED)
            .start(LocalDateTime.of(2000, 1, 1, 1, 1, 1))
            .end(LocalDateTime.of(2001, 2, 2, 2, 2, 2))
            .item(item)
            .booker(user1)
            .build();
    private final Booking bookingNext = Booking.builder()
            .status(StatusBooking.APPROVED)
            .start(LocalDateTime.of(3000, 1, 1, 1, 1, 1))
            .end(LocalDateTime.of(3001, 2, 2, 2, 2, 2))
            .item(item)
            .booker(user1)
            .build();

    @BeforeEach
    public void addBase() {
        userRepository.save(user1);
        userRepository.save(author);
        itemRepository.save(item);
        commentRepository.save(comment);
        bookingRepository.save(bookingNext);
        bookingRepository.save(bookingLast);
    }

    @Test
    void getItemOwnerIntegration_thenReturnItem() {
        List<ItemDto> result = itemService.getItemOwner(1, 0, 10);
        List<ItemDto> expected = Stream.of(item)
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setNextBooking(BookingMapper.toBookingDtoGivenWithBookerId(bookingNext)))
                .peek(itemDto -> itemDto.setLastBooking(BookingMapper.toBookingDtoGivenWithBookerId(bookingLast)))
                .peek(itemDto -> itemDto.setComments(List.of(CommentMapper.toCommentDto(comment))))
                .collect(Collectors.toList());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addItem_returnItemDto() {
        itemService.addItem(1, ItemDto.builder()
                .name("Вторая вещь")
                .description("description")
                .available(true)
                .build());

        ItemDto result = itemService.getItem(1, 2);

        assertThat(result.getName()).isEqualTo("Вторая вещь");
    }

    @Test
    void updateItem_returnItemDtoNameItemOne() {
        itemService.updateItem(1, 1, ItemDto.builder()
                .name("itemOne")
                .build());

        ItemDto result = itemService.getItem(1, 1);
        assertThat(result.getName()).isEqualTo("itemOne");
    }

    @AfterEach
    public void deleteItem() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}
