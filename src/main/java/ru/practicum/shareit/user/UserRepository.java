package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User getUserById(Integer id);

    User save(User user);

    User updateUser(User user, Integer id);

    void deleteUser(Integer id);

    boolean isUserPresentById(Integer id);

    boolean isEmailAlreadyInUse(String email);

    boolean isEmailAlreadyInUseForUpdate(String email, Integer id);
}
