package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.comment.Comment;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Item {
    private int id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private List<Comment> comments = new ArrayList<>();
    private Booking nextBooking;
    private Booking lastBooking;
    private Integer requestId;
}
