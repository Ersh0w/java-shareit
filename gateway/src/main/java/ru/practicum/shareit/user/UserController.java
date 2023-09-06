package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("получен запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("получен запрос на получение пользователя с id " + userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@RequestBody @Valid User user) {
        log.info("получен запрос на добавление пользователя с email = " + user.getEmail());
        return userClient.saveNewUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody User user, @PathVariable long userId) {
        log.info("получен запрос на обновление пользователя с id " + userId);
        return userClient.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("получен запрос на удаление пользователя с id " + userId);
        userClient.deleteUser(userId);
    }
}
