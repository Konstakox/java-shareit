package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.MarkerUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Запрос всех пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated(MarkerUserDto.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя {} ", userDto);
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable @Positive Integer userId) {
        log.info("Запрос пользователя по id {} ", userId);
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable @Positive Integer userId,
                                              @RequestBody @Validated(MarkerUserDto.OnUpdate.class) UserDto userDto) {
        log.info("Запрос изменения пользователя с id {}, изменение {}", userId, userDto);
        return ResponseEntity.ok(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Integer userId) {
        log.info("Запрос на удаление пользователя с id {} ", userId);
        userService.deleteUser(userId);
    }
}
