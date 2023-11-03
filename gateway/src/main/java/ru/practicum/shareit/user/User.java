package ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class User {

    private int id;
    private String name;
    private String email;
}
