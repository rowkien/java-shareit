package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(int userId);

    ItemDto getItem(int itemId);

    ItemDto createItem(int userId, Item item);

    ItemDto updateItem(int userId, int itemId, Item item);

    void deleteItem(int itemId);

    List<ItemDto> searchItems(int userId, String text);
}
