package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> dtoUsers = new ArrayList<>();
        repository.findAll().forEach(user -> dtoUsers.add(UserMapper.userMap(user)));
        return dtoUsers;
    }

    @Override
    public UserDto getUser(int userId) {
        UserDto userDto;
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            userDto = UserMapper.userMap(user.get());
        } else {
            throw new NotFoundException("Пользователя с ID " + userId + " нет в базе!");
        }
        return userDto;
    }

    @Override
    public UserDto createUser(User user) {
        isValid(user);
        return UserMapper.userMap(repository.save(user));
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
        return UserMapper.userMap(repository.save(updatedUser));
    }

    @Override
    public void deleteUser(int userId) {
        repository.deleteById(userId);
    }


    private void isValid(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым!");
        }
    }

    private void checkEmailDuplicate(String userEmail) {
        List<User> result = repository.findAll()
                .stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .collect(Collectors.toList());
        if (!result.isEmpty()) {
            throw new AlreadyExistsException("Такой email уже есть в базе!");
        }
    }

    private User checkUser(int userId) {
        List<User> result = repository.findAll()
                .stream()
                .filter(user -> user.getId() == userId)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new NotFoundException("Пользователя с id " + userId + " нет в базе!");
        }
        return result.get(0);
    }
}
