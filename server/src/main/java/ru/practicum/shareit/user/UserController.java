package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        log.info("получен запрос на получение пользователя с id " + userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public User saveNewUser(@RequestBody User user) {
        log.info("получен запрос на добавление пользователя с email = " + user.getEmail());
        return userService.saveUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody User user, @PathVariable long userId) {
        log.info("получен запрос на обновление пользователя с id " + userId);
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("получен запрос на удаление пользователя с id " + userId);
        userService.deleteUser(userId);
    }
}
