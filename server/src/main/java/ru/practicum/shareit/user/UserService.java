package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserDto userDto);

    UserDto getUser(Integer userId);

    void deleteUser(Integer userId);
}
