package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemRepository);
    }

    @Test
    public void shouldCreateBooking() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();
        Booking booking = Booking
                .builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);
        Assertions.assertEquals(booking.getBooker().getName(), bookingService.createBooking(1, bookingItemIdDto).getBooker().getName());
    }

    @Test
    public void shouldChangeBookingStatusApproved() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingService.changeBookingStatus(1, 1, true).getStatus());
    }

    @Test
    public void shouldChangeBookingStatusRejected() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Assertions.assertEquals(BookingStatus.REJECTED, bookingService.changeBookingStatus(1, 1, false).getStatus());
    }

    @Test
    public void shouldGetBooking() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        when(userService.getUser(anyInt())).thenReturn(booker);
        Assertions.assertEquals(1, bookingService.getBooking(2, 1).getId());
    }

    @Test
    public void shouldGetBookerBookingsAll() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getBookerBookings(2, "ALL", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetBookerBookingsFuture() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getBookerBookings(2, "FUTURE", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetBookerBookingsWaiting() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getBookerBookings(2, "WAITING", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetBookerBookingsRejected() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getBookerBookings(2, "REJECTED", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetBookerBookingsPast() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getBookerBookings(2, "PAST", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetBookerBookingsCurrent() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getBookerBookings(2, "CURRENT", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetOwnerBookingsAll() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getOwnerBookings(2, "ALL", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetOwnerBookingsFuture() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getOwnerBookings(2, "FUTURE", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetOwnerBookingsWaiting() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getOwnerBookings(2, "WAITING", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetOwnerBookingsRejected() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getOwnerBookings(2, "REJECTED", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetOwnerBookingsPast() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getOwnerBookings(2, "PAST", 0, 10).get(0).getId());
    }

    @Test
    public void shouldGetOwnerBookingsCurrent() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(owner))
                .available(true)
                .build();
        Booking booking = Booking
                .builder()
                .id(1)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .booker(UserMapper.userDtoMap(owner))
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();
        when(userService.getUser(anyInt())).thenReturn(booker);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        Assertions.assertEquals(1, bookingService.getOwnerBookings(2, "CURRENT", 0, 10).get(0).getId());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookingEnd() {
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .start(LocalDateTime.now())
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Завершение аренды не может быть пустым!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookingStart() {
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .end(LocalDateTime.now().plusDays(1))
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Начало аренды не может быть пустым!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookingEndInPast() {
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusDays(1))
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Завершение аренды не может быть в прошлом!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookingEndBeforeStart() {
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .start(LocalDateTime.now().plusDays(20))
                .end(LocalDateTime.now().plusDays(15))
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Завершение аренды не может быть перед ее стартом!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookingStartEqualsEnd() {
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Завершение аренды не может в одно время со стартом!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookingStartInPast() {
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(10))
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Начало аренды не может быть в прошлом!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithBadFromInPagination() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getBookerBookings(1, "ALL", -1, 1));
        Assertions.assertEquals("Индекс элемента не может быть меньше 0", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithBadSizeInPagination() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getBookerBookings(1, "ALL", 1, -1));
        Assertions.assertEquals("Количество страниц не может быть меньше 1!", exception.getMessage());
    }

    @Test
    public void shouldThrowUnsupportedStatusExceptionWithUnsupportedStatusBooker() {
        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.getBookerBookings(1, "UNSUPPORTED_STATUS", 0, 10));
        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    public void shouldThrowUnsupportedStatusExceptionWithUnsupportedStatusOwner() {
        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.getOwnerBookings(1, "UNSUPPORTED_STATUS", 0, 10));
        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    public void shouldThrowNotFoundExceptionWithBadUser() {
        User user = User
                .builder()
                .build();
        User owner = User
                .builder()
                .build();
        Item item = Item.
                builder()
                .owner(owner)
                .build();
        Booking booking = Booking
                .builder()
                .booker(user)
                .item(item)
                .build();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        when(userService.getUser(anyInt())).thenReturn(UserMapper.userMap(user));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1));
        Assertions.assertEquals("Пользователь с ID " + 1 + " не имеет доступа!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWheBookingStatusAlreadyChanged() {
        User user = User
                .builder()
                .build();
        User owner = User
                .builder()
                .build();
        Item item = Item
                .builder()
                .owner(owner)
                .build();
        Booking booking = Booking
                .builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.changeBookingStatus(1, 1, true));
        Assertions.assertEquals("У текущего бронирования уже обновлен статус!", exception.getMessage());
    }

    @Test
    public void shouldThrowNotFoundExceptionWheBookingOwnItem() {
        User user = User
                .builder()
                .id(1)
                .build();
        User owner = User
                .builder()
                .id(1)
                .build();
        Item item = Item
                .builder()
                .owner(owner)
                .build();
        Booking booking = Booking
                .builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.changeBookingStatus(1, 1, true));
        Assertions.assertEquals("Нельзя забронировать свою вещь!", exception.getMessage());
    }

    @Test
    public void shouldThrowNotFoundExceptionWithBadBookingIdWhenChangeBookingStatus() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.changeBookingStatus(1, 1, true));
        Assertions.assertEquals("В базе нет такого бронирования!", exception.getMessage());
    }

    @Test
    public void shouldThrowNotFoundExceptionWithBadBookingIdWhenCreateBooking() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(userService.getUser(1)).thenReturn(booker);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Предмета с ID" + bookingItemIdDto.getItemId() + " нет в базе!", exception.getMessage());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenOwnerBookedOwnItem() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Item item = Item
                .builder()
                .available(true)
                .owner(UserMapper.userDtoMap(booker))
                .build();
        when(userService.getUser(1)).thenReturn(booker);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Нельзя забронировать свою вещь!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionItemIsNotAvailable() {
        UserDto booker = UserDto
                .builder()
                .id(1)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        UserDto owner = UserDto
                .builder()
                .id(2)
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        BookingItemIdDto bookingItemIdDto = BookingItemIdDto
                .builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Item item = Item
                .builder()
                .available(false)
                .owner(UserMapper.userDtoMap(owner))
                .build();
        when(userService.getUser(1)).thenReturn(booker);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, bookingItemIdDto));
        Assertions.assertEquals("Нельзя забронировать недоступную вещь!", exception.getMessage());
    }

}