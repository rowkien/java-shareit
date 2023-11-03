package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                            @RequestBody ItemRequestDescriptionDto itemRequestDescriptionDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDescriptionDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemsRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getOwnItemsRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOthersItemsRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(value = "from", defaultValue = "0") int from,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return itemRequestService.getOthersItemsRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }


}
