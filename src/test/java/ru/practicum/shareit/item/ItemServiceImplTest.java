package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentTextDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository);
    }

    @Test
    public void shouldDeleteItem() {
        doNothing().when(itemRepository).deleteById(1);
        itemService.deleteItem(1);
        verify(itemRepository, times(1)).deleteById(1);
    }

    @Test
    public void shouldCreateItem() {
        UserDto userDto = UserDto
                .builder()
                .id(1)
                .name("User")
                .email("User@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();
        ItemDto itemDto = ItemDto
                .builder()
                .name("Item")
                .id(1)
                .description("ItemDescription")
                .available(true)
                .build();
        when(userService.getUser(1)).thenReturn(userDto);
        when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertEquals(itemDto.getName(), itemService.createItem(1, item).getName());
    }

    @Test
    public void shouldGetAllUsers() {
        UserDto userDto = UserDto
                .builder()
                .id(1)
                .name("User")
                .email("User@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(userDto))
                .available(true)
                .build();
        List<Item> items = List.of(item);
        when(userService.getUser(anyInt())).thenReturn(userDto);
        when(itemRepository.findAll()).thenReturn(items);
        Assertions.assertEquals(item.getName(), itemService.getAllItems(1, 0, 10).get(0).getName());
    }


    @Test
    public void shouldGetItem() {
        User author = User
                .builder()
                .name("author")
                .email("author@yandex.ru")
                .build();
        UserDto userDto = UserDto
                .builder()
                .id(1)
                .name("User")
                .email("User@yandex.ru")
                .build();
        Booking lastBooking = Booking
                .builder()
                .booker(UserMapper.userDtoMap(userDto))
                .build();
        Booking nextBooking = Booking
                .builder()
                .booker(UserMapper.userDtoMap(userDto))
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(UserMapper.userDtoMap(userDto))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .available(true)
                .build();
        Comment comment = Comment
                .builder()
                .item(item)
                .author(author)
                .build();
        item.setComments(List.of(comment));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByOwnerId(anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBookingByOwnerId(anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(nextBooking));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));
        Assertions.assertEquals(item.getName(), itemService.getItem(1, 1).getName());
    }

    @Test
    public void shouldUpdateItem() {
        User user = User
                .builder()
                .id(1)
                .name("author")
                .email("author@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .owner(user)
                .available(true)
                .build();
        when(userService.getUser(1)).thenReturn(UserMapper.userMap(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertEquals(item.getName(), itemService.updateItem(1, 1, item).getName());
    }

    @Test
    public void shouldAddComment() {
        User user = User
                .builder()
                .id(1)
                .name("author")
                .email("author@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .id(1)
                .description("ItemDescription")
                .owner(user)
                .available(true)
                .build();
        Comment comment = Comment
                .builder()
                .item(item)
                .author(user)
                .text("test")
                .build();
        CommentTextDto commentTextDto = CommentTextDto
                .builder()
                .text("test")
                .build();
        when(bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(true);
        when(userService.getUser(1)).thenReturn(UserMapper.userMap(user));
        when(itemRepository.findById(1)).thenReturn(Optional.ofNullable(item));
        when(commentRepository.save(any())).thenReturn(comment);
        Assertions.assertEquals(commentTextDto.getText(), itemService.addComment(1, 1, commentTextDto).getText());
    }

    @Test
    public void shouldGetEmptyListWithEmptySearchText() {
        List<ItemDto> searchedItems = new ArrayList<>();
        assertEquals(searchedItems, itemService.searchItems("", 0, 10));
    }

    @Test
    public void shouldGetSearchedItems() {
        User user = User
                .builder()
                .id(1)
                .name("author")
                .email("author@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .id(1)
                .description("ItemDescription")
                .owner(user)
                .available(true)
                .build();
        when(itemRepository.findAll()).thenReturn(List.of(item));
        Assertions.assertEquals(item.getDescription(), itemService.searchItems("ItemDescription", 0, 10).get(0).getDescription());
    }

    @Test
    public void shouldThrowNotFoundExceptionIfGetItemWithBadId() {
        when(itemRepository.findById(0)).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1, 0));
        Assertions.assertEquals("Предмета с id " + 0 + " нет в базе!", exception.getMessage());
    }

    @Test
    public void shouldThrowNotFoundExceptionIfUpdateItemWithoutOwner() {
        User user = User
                .builder()
                .id(1)
                .name("author")
                .email("author@yandex.ru")
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .id(1)
                .description("ItemDescription")
                .owner(user)
                .available(true)
                .build();
        when(itemRepository.findById(0)).thenReturn(Optional.of(item));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(2, 0, new Item()));
        Assertions.assertEquals("Редактировать вещь может только владелец!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyComment() {
        CommentTextDto commentTextDto = CommentTextDto
                .builder()
                .build();
        final ValidationException validationException = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1, 1, commentTextDto));
        Assertions.assertEquals("Комментарий не может быть пустым!", validationException.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyItemName() {
        Item item = Item
                .builder()
                .description("ItemDescription")
                .available(true)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.createItem(1, item));
        Assertions.assertEquals("Имя не может быть пустым!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyItemDescription() {
        Item item = Item
                .builder()
                .name("item")
                .available(true)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.createItem(1, item));
        Assertions.assertEquals("Описание не может быть пустым!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyItemAvailable() {
        Item item = Item
                .builder()
                .name("item")
                .description("description")
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.createItem(1, item));
        Assertions.assertEquals("Отсутствует поле с доступностью вещи", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithEmptyBookings() {
        CommentTextDto commentTextDto = CommentTextDto
                .builder()
                .text("test")
                .build();
        when(bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(false);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1, 1, commentTextDto));
        Assertions.assertEquals("Броней на вещь " + 1 + " нет в базе!", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithBadFromInPagination() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.searchItems("test", -1, 1));
        Assertions.assertEquals("Индекс элемента не может быть меньше 0", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithBadSizeInPagination() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.searchItems("test", 1, -1));
        Assertions.assertEquals("Количество страниц не может быть меньше 1!", exception.getMessage());
    }
}