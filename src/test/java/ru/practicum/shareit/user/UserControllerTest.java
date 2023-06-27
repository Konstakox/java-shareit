package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    private UserController userController;

//    @

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUsersCollectionInBody() {
        List<UserDto> expectedUserDto = List.of(
                UserDto.builder()
                        .name("test")
                        .email("test@test")
                        .build());
        Mockito.when(userService.getAllUsers()).thenReturn(expectedUserDto);

        List<UserDto> response = userController.getAllUsers();

//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expectedUserDto, response.getBody());
    }

    @Test
    void createUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void updateUser() {
    }
}