package ru.practicum.shareit.request;


import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(int userId, ItemRequestDescriptionDto itemRequestDescriptionDto);

    List<ItemRequestDto> getOwnItemsRequests(int userId);

    List<ItemRequestDto> getOthersItemsRequests(int userId, int from, int size);

    ItemRequestDto getItemRequest(int userId, int requestId);
}
