package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemBookingDto {
    private int id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private Booking nextBooking;
    private Booking lastBooking;
}
