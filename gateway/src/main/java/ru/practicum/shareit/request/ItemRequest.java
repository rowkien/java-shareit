package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequest {
    private int id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<Item> items = new ArrayList<>();
}
