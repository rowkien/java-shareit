package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public UserDto getUser(int userId) {
        return repository.getUser(userId);
    }

    @Override
    public UserDto createUser(User user) {
        isValid(user);
        return repository.createUser(user);
    }

    @Override
    public UserDto updateUser(User user, int userId) {
        return repository.updateUser(user, userId);
    }

    @Override
    public void deleteUser(int userId) {
        repository.deleteUser(userId);
    }


    private void isValid(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым!");
        }
    }
}
