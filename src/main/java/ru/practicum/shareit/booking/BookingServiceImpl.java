package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;

    private final BookingMapper bookingMapper;

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;

    @Override
    public BookingDto createBooking(int userId, BookingItemIdDto bookingItemIdDto) {
        isValid(bookingItemIdDto);
        UserDto bookerDto = userService.getUser(userId);
        User booker = userMapper.userDtoMap(bookerDto);
        ItemDto itemDto = itemService.getItem(userId, bookingItemIdDto.getItemId());
        Item item = itemMapper.itemDtoMap(itemDto);
        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать недоступную вещь!");
        }
        if (booker.getId() == item.getOwner().getId()) {
            throw new NotFoundException("Нельзя забронировать свою вещь!");
        }
        Booking booking = bookingMapper.bookingItemIdDtoMap(bookingItemIdDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.bookingMap(bookingRepository.save(booking));
    }

    @Override
    public BookingDto changeBookingStatus(int userId, int bookingId, boolean approved) {
        userService.getUser(userId);
        Booking booking = checkBooking(bookingId);
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("У текущего бронирования уже обновлен статус!");
        }
        if (booking.getBooker().getId() == userId) {
            throw new NotFoundException("Нельзя забронировать свою вещь!");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.bookingMap(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(int userId, int bookingId) {
        Booking booking = checkBooking(bookingId);
        userService.getUser(userId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.bookingMap(booking);
        } else {
            throw new NotFoundException("Пользователь с ID " + userId + " не имеет доступа!");
        }
    }

    @Override
    public List<BookingDto> getBookerBookings(int userId, String state) {
        userService.getUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        List<Booking> all = bookingRepository.findAll().stream().filter(booking ->
                booking.getBooker().getId() == userId).collect(Collectors.toList());
        List<BookingDto> allDto = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                break;
            case PAST:
                all = all.stream().filter(booking ->
                        booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case FUTURE:
                all = all.stream().filter(booking ->
                        booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case CURRENT:
                all = all.stream().filter(booking ->
                        booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case WAITING:
                all = all.stream().filter(booking ->
                        booking.getStatus() == BookingStatus.WAITING).collect(Collectors.toList());
                break;
            case REJECTED:
                all = all.stream().filter(booking ->
                        booking.getStatus() == BookingStatus.REJECTED).collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            default:
                throw new ValidationException("Такого статуса бронирования нет!");
        }
        all.forEach(booking -> allDto.add(bookingMapper.bookingMap(booking)));
        return allDto.stream().sorted(Comparator.comparing(BookingDto::getEnd).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(int userId, String state) {
        userService.getUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        List<Booking> all = bookingRepository.findAll().stream().filter(booking ->
                booking.getItem().getOwner().getId() == userId).collect(Collectors.toList());
        List<BookingDto> allDto = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                break;
            case PAST:
                all = all.stream().filter(booking ->
                        booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case FUTURE:
                all = all.stream().filter(booking ->
                        booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case CURRENT:
                all = all.stream().filter(booking ->
                        booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case WAITING:
                all = all.stream().filter(booking ->
                        booking.getStatus() == BookingStatus.WAITING).collect(Collectors.toList());
                break;
            case REJECTED:
                all = all.stream().filter(booking ->
                        booking.getStatus() == BookingStatus.REJECTED).collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            default:
                throw new ValidationException("Такого статуса бронирования нет!");
        }
        all.forEach(booking -> allDto.add(bookingMapper.bookingMap(booking)));
        return allDto.stream().sorted(Comparator.comparing(BookingDto::getEnd).reversed()).collect(Collectors.toList());
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

    private Booking checkBooking(int bookingId) {
        Booking booking;
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("В базе нет такого бронирования!");
        } else {
            booking = optionalBooking.get();
        }
        return booking;
    }
}
