package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentTextDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private Item item;

    private User user;

    private ItemDto itemDto;

    private List<ItemDto> itemDtoList;

    private Item itemToUpdate;

    private ItemDto updatedItemDto;

    private CommentTextDto commentTextDto;

    private CommentDto commentDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        user = User
                .builder()
                .id(1)
                .name("Owner")
                .email("Owner@mail.ru")
                .build();

        item = Item
                .builder()
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();

        itemDto = ItemDto
                .builder()
                .id(1)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();

        itemDtoList = new ArrayList<>();
        itemDtoList.add(itemDto);

        itemToUpdate = Item
                .builder()
                .name("updatedItem")
                .description("updaterItemDescription")
                .available(true)
                .build();

        updatedItemDto = ItemDto
                .builder()
                .id(1)
                .name("updatedItem")
                .description("updatedItem")
                .available(true)
                .build();

        commentTextDto = CommentTextDto
                .builder()
                .text("cool")
                .build();

        commentDto = CommentDto
                .builder()
                .id(1)
                .text("cool")
                .authorName(user.getName())
                .build();
    }

    @Test
    public void createItem() throws Exception {
        when(itemService.createItem(user.getId(), item))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

    }

    @Test
    public void getItem() throws Exception {
        when(itemService.getItem(user.getId(), itemDto.getId()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

    }

    @Test
    public void getAllItems() throws Exception {
        when(itemService.getAllItems(user.getId(), 0, 10))
                .thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoList.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0]name", is(itemDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoList.get(0).getAvailable())));

    }

    @Test
    public void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1);
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.updateItem(user.getId(), 1, itemToUpdate))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemToUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));

    }

    @Test
    public void searchItems() throws Exception {
        when(itemService.searchItems("ItemDescription", 0, 10))
                .thenReturn(itemDtoList);

        mockMvc.perform(get("/items/search?text=ItemDescription")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoList.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0]name", is(itemDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoList.get(0).getAvailable())));
    }

    @Test
    public void addComment() throws Exception {
        when(itemService.addComment(1, 1, commentTextDto))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentTextDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

    }
}