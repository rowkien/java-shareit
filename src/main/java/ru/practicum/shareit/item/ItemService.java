package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentTextDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(int userId);

    ItemDto getItem(int userId, int itemId);

    ItemDto createItem(int userId, Item item);

    ItemDto updateItem(int userId, int itemId, Item item);

    void deleteItem(int itemId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(int userId, int itemId, CommentTextDto comment);
}
