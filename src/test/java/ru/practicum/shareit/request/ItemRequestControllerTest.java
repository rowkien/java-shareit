package ru.practicum.shareit.request;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private ItemRequestDescriptionDto itemRequestDescriptionDto;

    private ItemRequestDto itemRequestDto;

    private List<ItemRequestDto> itemRequestDtoList;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequestDescriptionDto = ItemRequestDescriptionDto
                .builder()
                .description("TestDescription")
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .id(1)
                .description("TestDescription")
                .build();

        itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequestDto);


    }

    @Test
    public void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(1, itemRequestDescriptionDto))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDescriptionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

    }

    @Test
    public void getOwnItemsRequests() throws Exception {
        when(itemRequestService.getOwnItemsRequests(1))
                .thenReturn(itemRequestDtoList);
        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoList.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoList.get(0).getDescription())));
    }

    @Test
    public void getOthersItemsRequests() throws Exception {
        when(itemRequestService.getOthersItemsRequests(1, 0, 10))
                .thenReturn(itemRequestDtoList);
        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoList.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoList.get(0).getDescription())));
    }

    @Test
    public void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(1, 1))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

}