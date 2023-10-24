package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getAllItems(int userId, int from, int size) {
        isValidPagination(from, size);
        userService.getUser(userId);
        List<Item> userItems = itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .sorted(Comparator.comparing(Item::getId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        List<ItemDto> result = new ArrayList<>();
        for (Item item : userItems) {
            List<Booking> last = bookingRepository.findLastBookingByOwnerId(item.getId(),
                    userId, BookingStatus.REJECTED,
                    LocalDateTime.now());
            List<Booking> next = bookingRepository.findNextBookingByOwnerId(item.getId(),
                    userId, BookingStatus.REJECTED,
                    LocalDateTime.now());
            ItemDto itemDto = ItemMapper.itemMap(item);
            if (!last.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.bookingLastAndNextDtoMap(last.get(0)));
            }
            if (!next.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.bookingLastAndNextDtoMap(next.get(0)));
            }
            itemDto.setComments(CommentMapper.commentListMap(commentRepository.findAllByItemId(item.getId())));
            result.add(itemDto);
        }
        return result;
    }

    @Override
    public ItemDto getItem(int userId, int itemId) {
        Item item = checkItem(itemId);
        ItemDto itemDto = ItemMapper.itemMap(item);
        List<Booking> last =
                bookingRepository.findLastBookingByOwnerId(itemId, userId, BookingStatus.REJECTED, LocalDateTime.now());
        List<Booking> next =
                bookingRepository.findNextBookingByOwnerId(itemId, userId, BookingStatus.REJECTED, LocalDateTime.now());
        if (!last.isEmpty()) {
            itemDto.setLastBooking(BookingMapper.bookingLastAndNextDtoMap(last.get(0)));
        }
        if (!next.isEmpty()) {
            itemDto.setNextBooking(BookingMapper.bookingLastAndNextDtoMap(next.get(0)));
        }
        itemDto.setComments(CommentMapper.commentListMap(commentRepository.findAllByItemId(itemId)));
        return itemDto;
    }

    @Override
    public ItemDto createItem(int userId, Item item) {
        isValid(item);
        UserDto userDto = userService.getUser(userId);
        item.setOwner(UserMapper.userDtoMap(userDto));
        return ItemMapper.itemMap(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
        userService.getUser(userId);
        Item updatedItem = checkItem(itemId);
        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException("Редактировать вещь может только владелец!");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.itemMap(itemRepository.save(updatedItem));
    }

    @Override
    public void deleteItem(int itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text,int from, int size) {
        isValidPagination(from, size);
        List<ItemDto> result = new ArrayList<>();
        if (text.isEmpty()) {
            return result;
        }
        List<Item> searchedItems = itemRepository.findAll()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable())
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        searchedItems.forEach(item -> result.add(ItemMapper.itemMap(item)));
        return result;
    }

    private void isValid(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым!");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым!");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Отсутствует поле с доступностью вещи");
        }
    }

    private Item checkItem(int itemId) {
        Item item;
        Optional<Item> checkedItem = itemRepository.findById(itemId);
        if (checkedItem.isPresent()) {
            item = checkedItem.get();

        } else {
            throw new NotFoundException("Предмета с id " + itemId + " нет в базе!");
        }
        return item;
    }

    private void checkBookingByItemAndUserAndStatusAndPast(int userId, int itemId) {
        if (!bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now())) {
            throw new ValidationException("Броней на вещь " + itemId + " нет в базе!");
        }
    }

    public CommentDto addComment(int userId, int itemId, CommentTextDto commentTextDto) {
        if (commentTextDto.getText() == null || commentTextDto.getText().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым!");
        }
        checkBookingByItemAndUserAndStatusAndPast(userId, itemId);
        User user = UserMapper.userDtoMap(userService.getUser(userId));
        Item item = itemRepository.findById(itemId).get();
        Comment comment = CommentMapper.commentTextDtoMapping(commentTextDto, item, user);
        return CommentMapper.commentMap(commentRepository.save(comment));
    }

    private void isValidPagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс элемента не может быть меньше 0");
        } else if (size <= 0) {
            throw new ValidationException("Количество страниц не может быть меньше 1!");
        }
    }
}
