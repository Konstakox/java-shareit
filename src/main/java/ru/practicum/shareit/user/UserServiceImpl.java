package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.MyNotFoundException;
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
        List<User> allUsers = userRepository.findAll();
        log.info("Запрос всех пользователей выполнен");
        return allUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
//        User newUser = UserMapper.toUser(userDto);
        User newUser = userRepository.save(UserMapper.toUser(userDto));
        log.info("Пользователь создан с id {} ", newUser.getId());

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto getUser(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));

        log.info("Запрос пользователя по id {} выполнен, найден с id {} ", userId, user.getId());

        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        log.info("Запрос на удаление пользователя с id {} выполнен", userId);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyNotFoundException("Пользователь не найден ID: " + userId));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        user = userRepository.save(user);
        log.info("Пользователь с id {} изменён", userId);
        return UserMapper.toUserDto(user);
    }
}