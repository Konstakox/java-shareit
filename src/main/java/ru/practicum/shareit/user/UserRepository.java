package ru.practicum.shareit.user;

import java.util.List;

interface UserRepository {
    List<User> getAllUsers();

    User createUser(User user);

    User getUser(Integer userId);

    User updateUser(User user);

    Boolean existUserWithEmail(String emailDto);

    User deleteUser(Integer userId);
}