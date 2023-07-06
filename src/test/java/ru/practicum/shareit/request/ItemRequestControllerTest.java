package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constantsShareit.Constants.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("testDescription")
            .build();

    private final ItemRequestDto itemRequestDtoWithId = ItemRequestDto.builder()
            .id(1)
            .description("testDescription")
            .build();

    private final ItemRequestDtoWithItem itemRequestDtoWithIdAndItem = ItemRequestDtoWithItem.builder()
            .id(1)
            .description("testDescription")
            .items(List.of())
            .build();

    private final Integer userId = 1;

    @SneakyThrows
    @Test
    void addItemRequest_valid_thenResponseItemRequestDto() {
        when(itemRequestService.addItemRequest(userId, itemRequestDto)).thenReturn(itemRequestDtoWithId);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDtoWithId)));

        verify(itemRequestService, times(1)).addItemRequest(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void getYourItemRequests_thenResponseItemRequestDtoCollection() {
        when(itemRequestService.getYourItemRequests(userId)).thenReturn(List.of(itemRequestDtoWithIdAndItem));

        mockMvc.perform(get("/requests").header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithIdAndItem))));

        verify(itemRequestService, times(1)).getYourItemRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequests_thenResponseItemRequestDtoCollection() {
        when(itemRequestService.getAllItemRequests(anyInt(), any(), any())).thenReturn(List.of(itemRequestDtoWithIdAndItem));

        mockMvc.perform(get("/requests/all").header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithIdAndItem))));

        verify(itemRequestService, times(1)).getAllItemRequests(anyInt(), any(), any());
    }

    @SneakyThrows
    @Test
    void getItemRequest_validId_thenResponseItemRequestDto() {
        Integer requestId = 1;
        when(itemRequestService.getItemRequest(userId, requestId)).thenReturn(itemRequestDtoWithIdAndItem);

        mockMvc.perform(get(String.format("/requests/%d", requestId)).header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDtoWithIdAndItem)));

        verify(itemRequestService, times(1)).getItemRequest(userId, requestId);
    }

    @SneakyThrows
    @Test
    void getItemRequest_notValidId_thenResponseMyNotFoundExceptionStatusNotFound() {
        Integer requestId = 1;
        when(itemRequestService.getItemRequest(userId, requestId)).thenThrow(MyNotFoundException.class);

        mockMvc.perform(get(String.format("/requests/%d", requestId)).header(USER_ID, userId))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getItemRequest(userId, requestId);
    }
}