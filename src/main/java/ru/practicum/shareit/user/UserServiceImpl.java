package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository UserRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = UserRepository.getAllUsers();
        return allUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        newUser = UserRepository.createUser(newUser);

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto getUser(Integer userId) {
        User user = UserRepository.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден ID: " + userId);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public User deleteUser(Integer userId) {
        return UserRepository.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = UserRepository.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден ID: " + userId);
        }
        if (userDto.getEmail() != null) {
            Boolean existUser = UserRepository.existUserWithEmail(userDto.getEmail());
            if (existUser && !user.getEmail().equals(userDto.getEmail())) {
                throw new AlreadyExistsException("Пользователь с таким email уже существует, email: " + userDto.getEmail());
            } else {
                user.setEmail(userDto.getEmail());
            }
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        user = UserRepository.updateUser(user);
        return UserMapper.toUserDto(user);
    }
}