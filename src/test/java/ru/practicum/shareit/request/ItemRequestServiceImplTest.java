package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
    }

    @Test
    public void shouldCreateItemRequest() {
        User requestor = User
                .builder()
                .build();
        ItemRequestDescriptionDto itemRequestDescriptionDto = ItemRequestDescriptionDto
                .builder()
                .description("Description")
                .build();
        ItemRequest itemRequest = ItemRequest
                .builder()
                .description("Description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        when(userService.getUser(1)).thenReturn(UserMapper.userMap(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.createItemRequest(1, itemRequestDescriptionDto).getDescription());
    }

    @Test
    public void shouldGetItemRequest() {
        User requestor = User
                .builder()
                .build();
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1)
                .description("Description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        when(userService.getUser(1)).thenReturn(UserMapper.userMap(requestor));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequest));
        Assertions.assertEquals("Description", itemRequestService.getItemRequest(1, 1).getDescription());
    }

    @Test
    public void shouldGetOthersItemsRequests() {
        User requestor = User
                .builder()
                .build();
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1)
                .description("Description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();
        when(userService.getUser(anyInt())).thenReturn(UserMapper.userMap(requestor));
        when(itemRequestRepository.findAll()).thenReturn(List.of(itemRequest));
        when(itemRepository.findAll()).thenReturn(List.of(item));
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.getOthersItemsRequests(1, 0, 10).get(0).getDescription());
    }

    @Test
    public void shouldGetOwnItemsRequestsItemsRequests() {
        User requestor = User
                .builder()
                .id(1)
                .build();
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1)
                .description("Description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        Item item = Item
                .builder()
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();
        when(userService.getUser(anyInt())).thenReturn(UserMapper.userMap(requestor));
        when(itemRequestRepository.findAll()).thenReturn(List.of(itemRequest));
        when(itemRepository.findAll()).thenReturn(List.of(item));
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.getOwnItemsRequests(1).get(0).getDescription());
    }

    @Test
    public void shouldThrowValidationExceptionWithBadFromInPagination() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getOthersItemsRequests(1, -1, 0));
        Assertions.assertEquals("Индекс элемента не может быть меньше 0", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWithBadSizeInPagination() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getOthersItemsRequests(1, 1, -1));
        Assertions.assertEquals("Количество страниц не может быть меньше 1!", exception.getMessage());
    }


}