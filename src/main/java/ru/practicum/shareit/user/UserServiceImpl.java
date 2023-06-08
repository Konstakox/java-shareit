package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.getAllUsers();
        log.info("Запрос всех пользователей выполнен");
        return allUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        newUser = userRepository.createUser(newUser);
        log.info("Пользователь создан с id {} ", newUser.getId());

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto getUser(Integer userId) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            log.info("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден ID: " + userId);
        }
        log.info("Запрос пользователя по id {} выполнен, найден с id {} ", userId, user.getId());

        return UserMapper.toUserDto(user);
    }

    @Override
    public User deleteUser(Integer userId) {
        log.info("Запрос на удаление пользователя с id {} выполнен", userId);
        return userRepository.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            log.info("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден ID: " + userId);
        }
        if (userDto.getEmail() != null) {
            Boolean existUser = userRepository.existUserWithEmail(userDto.getEmail());
            if (existUser && !user.getEmail().equals(userDto.getEmail())) {
                log.info("Пользователь с таким email {} уже существует, id пользователя {}", user.getEmail(), userId);
                throw new AlreadyExistsException("Пользователь с таким email уже существует, email: " + userDto.getEmail());
            } else {
                user.setEmail(userDto.getEmail());
            }
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        user = userRepository.updateUser(user);
        log.info("Пользователь с id {} изменён", userId);
        return UserMapper.toUserDto(user);
    }
}