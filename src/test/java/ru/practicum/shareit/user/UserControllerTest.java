package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserDto createUserDto1 = UserDto.builder()
            .name("1testUserDto")
            .email("1testUserDto@ya.ru")
            .build();

    private final UserDto userDto1 = UserDto.builder()
            .id(1)
            .name("1testUserDto")
            .email("1testUserDto@ya.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .id(2)
            .name("2testUserDto")
            .email("2testUserDto@ya.ru")
            .build();

    @SneakyThrows
    @Test
    void getAllUsers_thenResponseStatusOkWithUsersCollectionEmpty() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getAllUsers_thenResponseUsersCollectionExistTwoUsers() {
        List<UserDto> expected = List.of(userDto1, userDto2);
        when(userService.getAllUsers()).thenReturn(expected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void createUser_valid_thenResponseUserDto() {
        when(userService.createUser(createUserDto1)).thenReturn(userDto1);

        mockMvc.perform(post("/users/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto1)));

        verify(userService, times(1)).createUser(createUserDto1);
    }

    @SneakyThrows
    @Test
    void getUser_validId_thenResponseUserDto() {
        Integer userId = 1;
        when(userService.getUser(userId)).thenReturn(userDto1);

        mockMvc.perform(get(String.format("/users/%d", userId)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto1)));

        verify(userService, times(1)).getUser(userId);
    }

    @SneakyThrows
    @Test
    void getUser_NotValidId_thenResponseMyNotFoundException() {
        Integer userId = 1;
        when(userService.getUser(anyInt())).thenThrow(MyNotFoundException.class);

        mockMvc.perform(get(String.format("/users/%d", userId)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(userId);
    }

    @SneakyThrows
    @Test
    void updateUser_thenResponseUsersUpdate() {
        when(userService.updateUser(1, userDto1)).thenReturn(userDto2);

        mockMvc.perform(patch(String.format("/users/%d", 1)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isOk())
                .andExpect(content().json((objectMapper.writeValueAsString(userDto2))));
        verify(userService, times(1)).updateUser(1, userDto1);
    }

    @SneakyThrows
    @Test
    void updateUser_notUser_thenResponseMyNotFoundException() {
        when(userService.updateUser(anyInt(), any())).thenThrow(MyNotFoundException.class);

        mockMvc.perform(patch(String.format("/users/%d", 1)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(1, userDto1);
    }

    @SneakyThrows
    @Test
    void deleteUser_thenResponseStatusOk() {
        mockMvc.perform(delete(String.format("/users/%d", 1))).andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1);
    }
}