package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.MyNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private final Integer userId1 = 1;
    private final Integer userId2 = 2;

    private final User user1 = User.builder()
            .name("nameTest1")
            .email("emailTest1@ya.ru")
            .build();

    private final UserDto userDto1notId = UserDto.builder()
            .name("nameTest1")
            .email("emailTest1@ya.ru")
            .build();

    @Test
    void getAllUsers_thenReturnUserCollections() {
        List<User> userList = List.of(user1);
        when(userRepository.findAll())
                .thenReturn(userList);

        assertEquals(userServiceImpl.getAllUsers(), userList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()));
    }

    @Test
    void createUser_whenUserFound_thenSaveUser() {
        when(userRepository.save(any())).thenReturn(user1);
        UserDto exuser = UserMapper.toUserDto(user1);

        UserDto expectedUserDto = userServiceImpl.createUser(exuser);

        assertEquals(expectedUserDto, UserMapper.toUserDto(user1));
        verify(userRepository).save(user1);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.of(user1));

        assertEquals((UserMapper.toUserDto(user1)), userServiceImpl.getUser(userId1));
    }

    @Test
    void getUser_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class, () -> userServiceImpl.getUser(userId1));
    }

    @Test
    void deleteUser_catchingRequestToRepository() {
        userServiceImpl.deleteUser(userId1);
        verify(userRepository).deleteById(userId1);
    }

    @Test
    void updateUser_thenReturnUserUpdate() {
        User updateUser = User.builder().name("updateName").email("updateEmail").build();
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(updateUser);

        UserDto expectedUserDto = userServiceImpl.updateUser(userId1, UserMapper.toUserDto(updateUser));

        assertEquals((UserMapper.toUserDto(updateUser)), expectedUserDto);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void updateUser_whenUserNotFound_thenMyNotFoundExceptionThrown() {
        when(userRepository.findById(userId1))
                .thenReturn(Optional.empty());

        assertThrows(MyNotFoundException.class,
                () -> userServiceImpl.updateUser(userId1, userDto1notId));
        verify(userRepository, never()).save(user1);
    }
}