package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestBody BookingItemIdDto bookingItemIdDto) {
        return bookingClient.createBooking(userId, bookingItemIdDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@RequestHeader("X-Sharer-User-Id") int userId,
                                                      @PathVariable int bookingId,
                                                      @RequestParam boolean approved) {
        return bookingClient.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(value = "from", defaultValue = "0") int from,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingClient.getBookerBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }

}
