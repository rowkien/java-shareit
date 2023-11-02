package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {
    public static BookingDto bookingMap(Booking booking) {
        return BookingDto
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking bookingItemIdDtoMap(BookingItemIdDto bookingItemIdDto, Item item, User booker) {
        return Booking
                .builder()
                .start(bookingItemIdDto.getStart())
                .end(bookingItemIdDto.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }

    public static BookingLastAndNextDto bookingLastAndNextDtoMap(Booking booking) {
        return BookingLastAndNextDto
                .builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .build();
    }
}
