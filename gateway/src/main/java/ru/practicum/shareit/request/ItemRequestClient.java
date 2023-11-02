package ru.practicum.shareit.request;

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
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(int userId, ItemRequestDescriptionDto itemRequestDescriptionDto) {
        isValidItemRequest(itemRequestDescriptionDto);
        return post("", userId, itemRequestDescriptionDto);
    }

    public ResponseEntity<Object> getOwnItemsRequests(int userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getOthersItemsRequests(int userId, int from, int size) {
        isValidPagination(from, size);
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("/all?from={from}&&size={size}", (long) userId, params);
    }

    public ResponseEntity<Object> getItemRequest(int userId, int requestId) {
        return get("/" + requestId, userId);
    }

    private void isValidPagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс элемента не может быть меньше 0");
        } else if (size <= 0) {
            throw new ValidationException("Количество страниц не может быть меньше 1!");
        }
    }

    private void isValidItemRequest(ItemRequestDescriptionDto itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым!");
        }
    }
}
