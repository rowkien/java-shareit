package ru.practicum.shareit.request;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems())
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequests.forEach(itemRequest -> itemRequestDtoList.add(toItemRequestDto(itemRequest)));
        return itemRequestDtoList;
    }

}
