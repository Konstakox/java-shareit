package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constantsShareit.Constants.USER_ID;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemDto itemDto = ItemDto.builder()
            .name("1testItemDto")
            .description("testDescription")
            .available(true)
            .build();

    private final ItemDto itemDtoWithId = ItemDto.builder()
            .id(1)
            .name("1testItemDto")
            .description("testDescription")
            .available(true)
            .build();

    private final Integer userId = 1;
    private final Integer itemId = 1;

    @SneakyThrows
    @Test
    void addItem_valid_thenResponseItemDto() {
        when(itemService.addItem(userId, itemDto)).thenReturn(itemDtoWithId);

        mockMvc.perform(post("/items").header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoWithId)));

        verify(itemService, times(1)).addItem(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void getItem_validId_thenResponseItemDto() {
        when(itemService.getItem(itemId, userId)).thenReturn(itemDtoWithId);

        mockMvc.perform(get(String.format("/items/%d", itemId)).header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoWithId)));

        verify(itemService, times(1)).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItem_notValidId_thenResponseMyNotFoundExceptionStatusNotFound() {
        when(itemService.getItem(anyInt(), anyInt())).thenThrow(MyNotFoundException.class);

        mockMvc.perform(get(String.format("/items/%d", itemId)).header(USER_ID, userId))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItemOwner_thenResponseItemDtoCollection() {
        List<ItemDto> expected = List.of(itemDtoWithId);
        when(itemService.getItemOwner(userId, 0, 10)).thenReturn(expected);

        mockMvc.perform(get("/items").header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(itemService, times(1)).getItemOwner(userId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getItemOwner_notExistUserId_thenResponseMyNotFoundExceptionStatusNotFound() {
        when(itemService.getItemOwner(anyInt(), anyInt(), anyInt())).thenThrow(MyNotFoundException.class);

        mockMvc.perform(get("/items").header(USER_ID, userId))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItemOwner(anyInt(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemOwner_notValidUserId_thenResponseBadRequestStatus() {
        mockMvc.perform(get("/items").header(USER_ID, "notValid"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItem_valid_thenResponseItemDto() {
        ItemDto updateItemDto = ItemDto.builder().description("update").build();
        when(itemService.updateItem(itemId, userId, updateItemDto)).thenReturn(itemDto);

        mockMvc.perform(patch(String.format("/items/%d", itemId)).header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemService, times(1)).updateItem(itemId, userId, updateItemDto);
    }

    @SneakyThrows
    @Test
    void updateItem_notExistItemDtoId__thenResponseMyNotFoundExceptionStatusNotFound() {
        ItemDto updateItemDto = ItemDto.builder().description("update").build();
        when(itemService.updateItem(itemId, userId, itemDto)).thenThrow(MyNotFoundException.class);

        mockMvc.perform(patch(String.format("/items/%d", itemId)).header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).updateItem(itemId, userId, itemDto);
    }

    @SneakyThrows
    @Test
    void searchItems_validText_thenResponseItemDtoCollection() {
        List<ItemDto> items = List.of();
        when(itemService.searchItems("Descrip", 0, 10)).thenReturn(items);

        mockMvc.perform(get("/items/search").queryParam("text", "Descrip"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService, times(1)).searchItems("Descrip", 0, 10);
    }

    @SneakyThrows
    @Test
    void searchItems_validTextEmpty_thenResponseItemDtoCollectionEmpty() {
        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search").queryParam("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));

        verify(itemService, times(1)).searchItems(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void comment_thenResponseCommentDto() {
        CommentDto commentDto = CommentDto.builder().text("comment").build();
        CommentDto commentDtoWithCommentDtoIdAndAuthorName = CommentDto.builder().id(1).text("comment")
                .authorName("authorName").created(LocalDateTime.now()).build();
        when(itemService.addComment(userId, itemId, commentDto)).thenReturn(commentDtoWithCommentDtoIdAndAuthorName);

        mockMvc.perform(post(String.format("/items/%d/comment", itemId)).header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDtoWithCommentDtoIdAndAuthorName)));

        verify(itemService, times(1)).addComment(userId, itemId, commentDto);
    }
}