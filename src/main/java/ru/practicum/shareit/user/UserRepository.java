package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<UserDto> getAllUsers();

    UserDto getUser(int userId);

    UserDto createUser(User user);

    UserDto updateUser(User user, int userId);

    void deleteUser(int userId);
}
