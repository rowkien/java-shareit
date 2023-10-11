package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(int userId, BookingItemIdDto bookingItemIdDto);

    BookingDto changeBookingStatus(int userId, int bookingId, boolean approved);

    BookingDto getBooking(int userId, int bookingId);

    List<BookingDto> getBookerBookings(int userId, String state);

    List<BookingDto> getOwnerBookings(int userId, String state);
}
