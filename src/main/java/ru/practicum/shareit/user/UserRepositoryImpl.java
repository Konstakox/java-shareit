package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private Integer id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        if (users.containsValue(user)) {
            throw new AlreadyExistsException("Пользователь " + user.getName() + " уже существует.");
        }
        String email = user.getEmail();
        boolean existMail = false;
        for (User value : users.values()) {
            existMail = value.getEmail().equals(email);
        }
        if (existMail) {
            throw new AlreadyExistsException("Пользователь с таким EMAIL" + email + " уже существует.");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Integer userId) {
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Boolean existUserWithEmail(String emailDto) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(emailDto));
    }

    @Override
    public User deleteUser(Integer userId) {
        return users.remove(userId);
    }
}
