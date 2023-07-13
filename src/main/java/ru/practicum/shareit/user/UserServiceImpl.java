package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyInUseException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        if (!isUserPresentById(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return userRepository.getUserById(id);
    }

    @Override
    public User saveUser(User user) {
        if (isEmailAlreadyInUse(user.getEmail())) {
            throw new EmailAlreadyInUseException("Данный email уже используется");
        }
        User createdUser = userRepository.save(user);
        log.info("Добавлен пользователь: {}", createdUser.toString());
        return createdUser;
    }

    @Override
    public User updateUser(User user, Integer id) {
        User userToReturn;
        if (isUserPresentById(id)) {
            if (isEmailAlreadyInUseForUpdate(user.getEmail(), id)) {
                throw new EmailAlreadyInUseException("Данный email уже используется");
            }
            userToReturn = userRepository.updateUser(user, id);
            log.info("Обновлен пользователь: {}", user.toString());
        } else {
            log.info("Ошибка обновления пользователя: Пользователь {} не найден", user.toString());
            throw new UserNotFoundException("Пользователь не найден");
        }

        return userToReturn;
    }

    @Override
    public void deleteUser(Integer id) {
        if (!isUserPresentById(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteUser(id);
        log.info("Удален пользователь с id: " + id);
    }

    private boolean isUserPresentById(Integer id) {
        return userRepository.isUserPresentById(id);
    }

    private boolean isEmailAlreadyInUse(String email) {
        return userRepository.isEmailAlreadyInUse(email);
    }

    private boolean isEmailAlreadyInUseForUpdate(String email, Integer id) {
        return userRepository.isEmailAlreadyInUseForUpdate(email, id);
    }
}
