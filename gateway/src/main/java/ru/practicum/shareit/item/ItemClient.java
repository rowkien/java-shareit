package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItems(int userId, int from, int size) {
        isValidPagination(from, size);
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", (long) userId, params);
    }

    public ResponseEntity<Object> getItem(int userId, int itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(int userId, Item item) {
        isValid(item);
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(int userId, int itemId, Item item) {
        return patch("/" + itemId, userId, item);
    }

    public void deleteItem(int itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(String text, int from, int size) {
        isValidPagination(from, size);
        Map<String, Object> params = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", 1L, params);
    }

    public ResponseEntity<Object> addComment(int userId, int itemId, CommentTextDto commentTextDto) {
        return post("/" + itemId + "/comment", userId, commentTextDto);
    }

    private void isValidPagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс элемента не может быть меньше 0");
        } else if (size <= 0) {
            throw new ValidationException("Количество страниц не может быть меньше 1!");
        }
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
}
