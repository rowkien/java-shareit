package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserService userService;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(int userId, ItemRequestDescriptionDto itemRequestDescriptionDto) {
        isValidItemRequest(itemRequestDescriptionDto);
        User requestor = UserMapper.userDtoMap(userService.getUser(userId));
        ItemRequest itemRequest = ItemRequest
                .builder()
                .description(itemRequestDescriptionDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnItemsRequests(int userId) {
        userService.getUser(userId);
        List<ItemRequest> userRequests = itemRequestRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .filter(itemRequest -> itemRequest.getRequestor().getId() == userId).collect(Collectors.toList());
        userRequests.forEach(this::setItemsByAnswers);
        return ItemRequestMapper.toItemRequestDtoList(userRequests);
    }

    @Override
    public List<ItemRequestDto> getOthersItemsRequests(int userId, int from, int size) {
        isValidPagination(from, size);
        userService.getUser(userId);
        List<ItemRequest> othersItemsRequests = itemRequestRepository
                .findAll()
                .stream()
                .filter(itemRequest -> itemRequest.getRequestor().getId() != userId)
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        othersItemsRequests.forEach(this::setItemsByAnswers);
        return ItemRequestMapper.toItemRequestDtoList(othersItemsRequests);
    }

    @Override
    public ItemRequestDto getItemRequest(int userId, int requestId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id " + requestId + " нет в базе!"));
        setItemsByAnswers(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private void isValidItemRequest(ItemRequestDescriptionDto itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым!");
        }
    }

    private void isValidPagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс элемента не может быть меньше 0");
        } else if (size <= 0) {
            throw new ValidationException("Количество страниц не может быть меньше 1!");
        }
    }

    private void setItemsByAnswers(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findAll();
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getRequestId() != null && (itemRequest.getId() == item.getRequestId())) {
                result.add(item);
            }
        }
        itemRequest.setItems(result);
    }
}
