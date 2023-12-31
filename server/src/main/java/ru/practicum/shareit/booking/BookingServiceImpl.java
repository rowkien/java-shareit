package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(int userId, BookingItemIdDto bookingItemIdDto) {
        Item item;
        isValid(bookingItemIdDto);
        UserDto bookerDto = userService.getUser(userId);
        User booker = UserMapper.userDtoMap(bookerDto);
        Optional<Item> optionalItem = itemRepository.findById(bookingItemIdDto.getItemId());
        if (optionalItem.isPresent()) {
            item = optionalItem.get();
        } else {
            throw new NotFoundException("Предмета с ID" + bookingItemIdDto.getItemId() + " нет в базе!");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать недоступную вещь!");
        }
        if (booker.getId() == item.getOwner().getId()) {
            throw new NotFoundException("Нельзя забронировать свою вещь!");
        }
        Booking booking = BookingMapper.bookingItemIdDtoMap(bookingItemIdDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.bookingMap(bookingRepository.save(booking));
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
        return BookingMapper.bookingMap(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(int userId, int bookingId) {
        Booking booking = checkBooking(bookingId);
        userService.getUser(userId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.bookingMap(booking);
        } else {
            throw new NotFoundException("Пользователь с ID " + userId + " не имеет доступа!");
        }
    }

    @Override
    public List<BookingDto> getBookerBookings(int userId, String state, int from, int size) {
        isValidPagination(from, size);
        userService.getUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        BookingState bookingState = BookingState.valueOf(state);
        List<Booking> bookings = new ArrayList<>();
        List<BookingDto> bookingsDto = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getBooker().getId() == userId)
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getBooker().getId() == userId)
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getBooker().getId() == userId)
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getBooker().getId() == userId)
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING).collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getBooker().getId() == userId)
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED).collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        bookings.forEach(booking -> bookingsDto.add(BookingMapper.bookingMap(booking)));
        return bookingsDto;
    }

    @Override
    public List<BookingDto> getOwnerBookings(int userId, String state, int from, int size) {
        isValidPagination(from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        userService.getUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        List<Booking> bookings = new ArrayList<>();
        List<BookingDto> bookingsDto = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getItem().getOwner().getId() == userId)
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getItem().getOwner().getId() == userId)
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getItem().getOwner().getId() == userId)
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getItem().getOwner().getId() == userId)
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING).collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findAll(pageRequest).getContent()
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .filter(booking -> booking.getItem().getOwner().getId() == userId)
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED).collect(Collectors.toList());
                break;
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        bookings.forEach(booking -> bookingsDto.add(BookingMapper.bookingMap(booking)));
        return bookingsDto;
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

    private void isValidPagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс элемента не может быть меньше 0");
        } else if (size <= 0) {
            throw new ValidationException("Количество страниц не может быть меньше 1!");
        }
    }
}
