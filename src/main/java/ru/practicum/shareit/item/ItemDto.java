package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private User owner;
    private boolean isAvailable;
}
