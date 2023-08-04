package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers() {
        List<User> expectedUsers = List.of(new User());
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(new User()));

        List<User> users = userController.getAllUsers();

        assertEquals(expectedUsers, users);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById() {
        User expectedUser = new User();
        expectedUser.setId(1);
        Mockito.when(userService.getUserById(1)).thenReturn(expectedUser);

        User user = userController.getUserById(1);

        assertEquals(expectedUser, user);
        verify(userService, times(1)).getUserById(expectedUser.getId());
    }

    @Test
    void saveNewUser() {
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("user@mail.com");
        Mockito.when(userService.saveUser(expectedUser)).thenReturn(expectedUser);

        User user = userController.saveNewUser(expectedUser);

        assertEquals(expectedUser, user);
        verify(userService, times(1)).saveUser(expectedUser);
    }

    @Test
    void updateUser() {
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("user@mail.com");
        Mockito.when(userService.updateUser(expectedUser, expectedUser.getId())).thenReturn(expectedUser);

        User user = userController.updateUser(expectedUser, expectedUser.getId());

        assertEquals(expectedUser, user);
        verify(userService, times(1)).updateUser(expectedUser, expectedUser.getId());
    }

    @Test
    void deleteUser() {
        long userId = 1;

        userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
    }
}
