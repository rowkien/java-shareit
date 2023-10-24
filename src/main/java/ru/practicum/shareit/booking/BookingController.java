package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestBody BookingItemIdDto bookingItemIdDto) {
        return bookingService.createBooking(userId, bookingItemIdDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId,
                                          @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(value = "from", defaultValue = "0") int from,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.getBookerBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
