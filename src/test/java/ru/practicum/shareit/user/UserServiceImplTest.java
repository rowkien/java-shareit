package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void shouldCreateUser() {
        User user = User
                .builder()
                .name("User")
                .email("User@yandex.ru")
                .build();
        UserDto userDto = UserMapper.userMap(user);
        when(userRepository.save(user)).thenReturn(user);
        Assertions.assertEquals(userDto, userService.createUser(user));
    }

    @Test
    public void shouldGetAllUsers() {
        User user = User
                .builder()
                .name("User")
                .email("User@yandex.ru")
                .build();
        List<User> users = new ArrayList<>();
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(UserMapper.userMap(user));
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);
        Assertions.assertEquals(userDtoList, userService.getAllUsers());
    }

    @Test
    public void shouldGetUser() {
        UserDto user = UserDto
                .builder()
                .id(1)
                .name("User")
                .email("User@yandex.ru")
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(UserMapper.userDtoMap(user)));
        Assertions.assertEquals(1, userService.getUser(1).getId());
    }

    @Test
    public void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1);
        userService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    public void shouldUpdateUser() {
        User user = User
                .builder()
                .id(1)
                .name("User")
                .email("User@yandex.ru")
                .build();
        List<User> userList = List.of(user);
        when(userRepository.findAll()).thenReturn(userList);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Assertions.assertEquals(user.getName(), userService.updateUser(user, 1).getName());
    }

    @Test
    public void shouldThrowNotFoundExceptionIfGetUserWithBadId() {
        when(userRepository.findById(0)).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(anyInt()));
        Assertions.assertEquals("Пользователя с ID " + 0 + " нет в базе!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyUserEmail() {
        User user = User
                .builder()
                .name("user")
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(user));
        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyUserName() {
        User user = User
                .builder()
                .email("userMail@yandex.ru")
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(user));
        Assertions.assertEquals("Имя не может быть пустым!", exception.getMessage());
    }
}