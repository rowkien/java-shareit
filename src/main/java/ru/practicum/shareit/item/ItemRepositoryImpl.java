package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;

    private static final List<Item> items = new ArrayList<>();

    private int nextId = 1;

    @Override
    public List<ItemDto> getAllItems(int userId) {
        List<Item> userItems = items
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
        List<ItemDto> result = new ArrayList<>();
        userItems.forEach(item -> result.add(itemMapper.itemMap(item)));
        return result;
    }

    @Override
    public ItemDto getItem(int itemId) {
        Item item = checkItem(itemId);
        return itemMapper.itemMap(item);
    }

    @Override
    public ItemDto createItem(int userId, Item item) {
        item.setId(nextId++);
        items.add(item);
        return itemMapper.itemMap(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
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
        return itemMapper.itemMap(updatedItem);
    }

    @Override
    public void deleteItem(int itemId) {
        items.remove(checkItem(itemId));
    }

    @Override
    public List<ItemDto> searchItems(int userId, String text) {
        List<ItemDto> result = new ArrayList<>();
        if (text.isEmpty()) {
            return result;
        }
        List<Item> searchedItems = items
                .stream()
                .filter(item -> item.getOwner().getId() == userId
                        && item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable())
                .collect(Collectors.toList());
        searchedItems.forEach(item -> result.add(itemMapper.itemMap(item)));
        return result;
    }

    private Item checkItem(int itemId) {
        List<Item> result = items.stream().filter(item -> item.getId() == itemId).collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new NotFoundException("Предмета с id " + itemId + " нет в базе!");
        }
        return result.get(0);
    }
}
