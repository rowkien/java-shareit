package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(int userId, BookingItemIdDto bookingItemIdDto) {
        isValid(bookingItemIdDto);
        return post("", userId, bookingItemIdDto);
    }

    public ResponseEntity<Object> changeBookingStatus(int userId, int bookingId, boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", (long) userId, params, null);
    }

    public ResponseEntity<Object> getBooking(int userId, int bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookerBookings(int userId, String state, int from, int size) {
        isValidPagination(from, size);
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&&from={from}&&size={size}", (long) userId, params);
    }

    public ResponseEntity<Object> getOwnerBookings(int userId, String state, int from, int size) {
        isValidPagination(from, size);
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        return get("/owner?state={state}&&from={from}&&size={size}", (long) userId, params);
    }

    private void isValidPagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс элемента не может быть меньше 0");
        } else if (size <= 0) {
            throw new ValidationException("Количество страниц не может быть меньше 1!");
        }
    }

    private void isValid(BookingItemIdDto bookingItemIdDto) {
        if (bookingItemIdDto.getEnd() == null) {
            throw new ValidationException("Завершение аренды не может быть пустым!");
        } else if (bookingItemIdDto.getStart() == null) {
            throw new ValidationException("Начало аренды не может быть пустым!");
        } else if (bookingItemIdDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Завершение аренды не может быть в прошлом!");
        } else if (bookingItemIdDto.getEnd().isBefore(bookingItemIdDto.getStart())) {
            throw new ValidationException("Завершение аренды не может быть перед ее стартом!");
        } else if (bookingItemIdDto.getEnd().equals(bookingItemIdDto.getStart())) {
            throw new ValidationException("Завершение аренды не может в одно время со стартом!");
        } else if (bookingItemIdDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало аренды не может быть в прошлом!");
        }
    }
}
