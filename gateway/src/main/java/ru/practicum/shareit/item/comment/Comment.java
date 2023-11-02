package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Comment {

    private int id;
    private String text;

    private Item item;

    private User author;
    private LocalDateTime created;
}
