package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    public UserDto userMap(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
