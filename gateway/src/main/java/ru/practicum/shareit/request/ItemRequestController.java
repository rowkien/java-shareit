package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestBody ItemRequestDescriptionDto itemRequestDescriptionDto) {
        return itemRequestClient.createItemRequest(userId, itemRequestDescriptionDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemsRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestClient.getOwnItemsRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOthersItemsRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                                         @RequestParam(value = "from", defaultValue = "0") int from,
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        return itemRequestClient.getOthersItemsRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @PathVariable int requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
