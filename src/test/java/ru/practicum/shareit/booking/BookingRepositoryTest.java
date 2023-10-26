package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void shouldFindLastBookingByOwnerId() {
        User owner = User
                .builder()
                .name("owner")
                .email("email@yandex.ru")
                .build();
        userRepository.save(owner);
        User booker = User
                .builder()
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        userRepository.save(booker);
        Item item = Item
                .builder()
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        itemRepository.save(item);
        Booking booking = Booking
                .builder()
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(10))
                .build();
        bookingRepository.save(booking);
        Assertions.assertNotNull(bookingRepository.findLastBookingByOwnerId(item.getId(), owner.getId(), BookingStatus.REJECTED, LocalDateTime.now()));
    }

    @Test
    public void shouldFindNextBookingByOwnerId() {
        User owner = User
                .builder()
                .name("owner")
                .email("email@yandex.ru")
                .build();
        userRepository.save(owner);
        User booker = User
                .builder()
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        userRepository.save(booker);
        Item item = Item
                .builder()
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        itemRepository.save(item);
        Booking booking = Booking
                .builder()
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(20))
                .build();
        bookingRepository.save(booking);
        Assertions.assertNotNull(bookingRepository.findNextBookingByOwnerId(item.getId(), owner.getId(), BookingStatus.REJECTED, LocalDateTime.now()));
    }

    @Test
    public void shouldExistsBookingByBookerIdAndItemIdAndStatusAndStartBefore() {
        User owner = User
                .builder()
                .name("owner")
                .email("email@yandex.ru")
                .build();
        userRepository.save(owner);
        User booker = User
                .builder()
                .name("booker")
                .email("booker@yandex.ru")
                .build();
        userRepository.save(booker);
        Item item = Item
                .builder()
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        itemRepository.save(item);
        Booking booking = Booking
                .builder()
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().minusDays(10))
                .build();
        bookingRepository.save(booking);
        Assertions.assertFalse(bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(booker.getId(),item.getId(),BookingStatus.WAITING,LocalDateTime.now()));
    }
}