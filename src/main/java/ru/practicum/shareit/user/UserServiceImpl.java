package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        User createdUser = userRepository.save(user);
        log.info("Добавлен пользователь: {}", createdUser.toString());
        return createdUser;
    }

    @Override
    @Transactional
    public User updateUser(User user, long id) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        return userRepository.save(userToUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
        log.info("Удален пользователь с id: " + id);
    }
}
