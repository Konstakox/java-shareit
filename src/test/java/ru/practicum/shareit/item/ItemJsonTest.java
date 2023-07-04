package ru.practicum.shareit.item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemJsonTest {
    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("name")
            .description("description")
            .available(true)
            .requestId(1)
            .nextBooking(null)
            .lastBooking(null)
            .comments(null)
            .build();

    @SneakyThrows
    @Test
    void testSerializeItemDto() {
        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathStringValue("$.comments").isNull();
    }

    @SneakyThrows
    @Test
    void testDeserializeItem()  {
        String content = "{\"nextBooking\": null" +
                ",\"lastBooking\": null " +
                ",\"comments\": null" +
                ",\"id\":1" +
                ",\"name\":\"name\"" +
                ",\"description\":\"description\"" +
                ",\"available\":true" +
                ",\"requestId\":1}";

        ItemDto expected = jsonItemDto.parse(content).getObject();
        assertEquals(expected, itemDto);
    }
}
