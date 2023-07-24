package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(long id);

    User saveUser(User user);

    User updateUser(User user, long id);

    void deleteUser(long id);
}
