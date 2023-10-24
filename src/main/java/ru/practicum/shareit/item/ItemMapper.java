package ru.practicum.shareit.item;

public class ItemMapper {

    public static ItemDto itemMap(Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequestId())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .build();
    }

    public static Item itemDtoMap(ItemDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .requestId(itemDto.getRequestId())
                .available(itemDto.getAvailable())
                .build();
    }
}
