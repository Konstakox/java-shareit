package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.MarkerUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Validated(MarkerUserDto.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя {} ", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Integer userId) {
        log.info("Запрос пользователя по id {} ", userId);
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId,
                              @RequestBody @Validated(MarkerUserDto.OnUpdate.class) UserDto userDto) {
        log.info("Запрос изменения пользователя с id {}, изменение {}", userId, userDto);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Запрос на удаление пользователя с id {} ", userId);
        userService.deleteUser(userId);
    }
}
