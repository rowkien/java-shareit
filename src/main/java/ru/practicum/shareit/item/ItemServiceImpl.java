package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public List<ItemDto> getAllItems(int userId) {
        userService.getUser(userId);
        return itemRepository.getAllItems(userId);
    }

    @Override
    public ItemDto getItem(int itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public ItemDto createItem(int userId, Item item) {
        item.setOwner(addOwner(userId));
        isValid(item);
        return itemRepository.createItem(userId, item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
        userService.getUser(userId);
        return itemRepository.updateItem(userId, itemId, item);
    }

    @Override
    public void deleteItem(int itemId) {
        itemRepository.deleteItem(itemId);
    }

    @Override
    public List<ItemDto> searchItems(int userId, String text) {
        return itemRepository.searchItems(userId, text);
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

    private User addOwner(int userId) {
        UserDto ownerDto = userService.getUser(userId);
        return User.builder()
                .id(ownerDto.getId())
                .name(ownerDto.getName())
                .email(ownerDto.getEmail())
                .build();
    }

}
