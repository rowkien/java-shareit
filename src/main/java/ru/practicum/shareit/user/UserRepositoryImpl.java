package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;

    private static final List<User> users = new ArrayList<>();

    private int nextId = 1;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> dtoUsers = new ArrayList<>();
        users.forEach(user -> dtoUsers.add(userMapper.userMap(user)));
        return dtoUsers;
    }

    @Override
    public UserDto getUser(int userId) {
        return userMapper.userMap(checkUser(userId));
    }

    @Override
    public UserDto createUser(User user) {
        checkEmailDuplicate(user.getEmail());
        user.setId(nextId++);
        users.add(user);
        return userMapper.userMap(user);
    }

    @Override
    public UserDto updateUser(User user, int userId) {
        User updatedUser = checkUser(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (updatedUser.getEmail().equals(user.getEmail())) {
                updatedUser.setEmail(user.getEmail());
            } else {
                checkEmailDuplicate(user.getEmail());
                updatedUser.setEmail(user.getEmail());
            }
        }
        return userMapper.userMap(updatedUser);
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(checkUser(userId));
    }

    private User checkUser(int userId) {
        List<User> result = users
                .stream()
                .filter(user -> user.getId() == userId)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new NotFoundException("Пользователя с id " + userId + " нет в базе!");
        }
        return result.get(0);
    }

    private void checkEmailDuplicate(String userEmail) {
        List<User> result = users
                .stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .collect(Collectors.toList());
        if (!result.isEmpty()) {
            throw new ValidationException("Такой email уже есть в базе!");
        }
    }
}
