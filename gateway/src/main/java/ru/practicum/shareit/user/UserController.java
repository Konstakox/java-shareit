package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.MarkerUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос всех пользователей");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(MarkerUserDto.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя {} ", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive Integer userId) {
        log.info("Запрос пользователя по id {} ", userId);
        return userClient.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive Integer userId,
                                             @RequestBody @Validated(MarkerUserDto.OnUpdate.class) UserDto userDto) {
        log.info("Запрос изменения пользователя с id {}, изменение {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Integer userId) {
        log.info("Запрос на удаление пользователя с id {} ", userId);
        return userClient.deleteUser(userId);
    }
}
