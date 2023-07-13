package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer idCounter = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, Integer id) {
        User userToUpdate = users.get(id);
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        users.put(id, userToUpdate);
        return userToUpdate;
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public boolean isUserPresentById(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        if (users.values() != null) {
            for (User user : users.values()) {
                if (user.getEmail().equals(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEmailAlreadyInUseForUpdate(String email, Integer id) {
        if (users.values() != null) {
            for (User user : users.values()) {
                if (user.getEmail().equals(email) && !Objects.equals(user.getId(), id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer getId() {
        idCounter++;
        return idCounter;
    }
}
