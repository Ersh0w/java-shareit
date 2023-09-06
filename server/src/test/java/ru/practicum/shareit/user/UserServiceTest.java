package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User userIn;
    User userOut;
    long userId = 1;

    @BeforeEach
    void setUp() {
        userIn = User.builder()
                .name("name")
                .email("name@mail.ru")
                .build();

        userOut = User.builder()
                .id(1)
                .name("name")
                .email("name@mail.ru")
                .build();
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userOut));

        List<User> usersList = userService.getAllUsers();

        assertEquals(List.of(userOut), usersList);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_shouldBeFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userOut));

        User user = userService.getUserById(userId);

        assertEquals(userOut, user);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_whenNotFound_fail() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void saveUser_shouldAdd() {
        when(userRepository.save(userIn)).thenReturn(userOut);

        User user = userService.saveUser(userIn);

        assertEquals(userOut, user);
        verify(userRepository).save(userIn);
    }

    @Test
    void updateUser_shouldBeUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userOut));
        when(userRepository.save(userOut)).thenReturn(userOut);

        User updatedUser = userService.updateUser(userOut, userId);

        assertEquals(userOut, updatedUser);
        verify(userRepository).findById(userId);
        verify(userRepository).save(userOut);
    }

    @Test
    void deleteUser_shouldDelete() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_userNotFoundFail() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).existsById(userId);
    }
}
