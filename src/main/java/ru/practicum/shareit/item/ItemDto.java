package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingLastAndNextDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;


@Data
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private List<CommentDto> comments;
    private BookingLastAndNextDto nextBooking;
    private BookingLastAndNextDto lastBooking;
    private Integer requestId;

}
