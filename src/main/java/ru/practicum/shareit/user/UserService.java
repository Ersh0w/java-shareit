package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer id);

    User saveUser(User user);

    User updateUser(User user, Integer id);

    void deleteUser(Integer id);
}
