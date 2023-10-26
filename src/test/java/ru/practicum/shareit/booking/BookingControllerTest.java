package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.Item;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private BookingItemIdDto bookingItemIdDto;

    private BookingDto bookingDto;

    private List<BookingDto> bookingDtoList;

    private BookingDto bookingDtoUpdated;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        Item item = Item
                .builder()
                .id(1)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();

        bookingItemIdDto = BookingItemIdDto
                .builder()
                .itemId(1)
                .build();

        bookingDto = BookingDto
                .builder()
                .id(1)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoList = new ArrayList<>();
        bookingDtoList.add(bookingDto);

        bookingDtoUpdated = BookingDto
                .builder()
                .id(1)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    public void createBooking() throws Exception {
        when(bookingService.createBooking(1, bookingItemIdDto))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingItemIdDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())));
    }

    @Test
    public void getBooking() throws Exception {
        when(bookingService.getBooking(1, bookingDto.getId()))
                .thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())));
    }

    @Test
    public void changeBookingStatusOnApproved() throws Exception {
        when(bookingService.changeBookingStatus(1, 1, true))
                .thenReturn(bookingDtoUpdated);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoUpdated.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoUpdated.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoUpdated.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoUpdated.getItem().getDescription())));

    }

    @Test
    public void getBookerBookings() throws Exception {
        when(bookingService.getBookerBookings(1, "ALL", 0, 10))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoList.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoList.get(0).getStatus().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoList.get(0).getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoList.get(0).getItem().getName())))
                .andExpect(jsonPath("$[0].item.description",
                        is(bookingDtoList.get(0).getItem().getDescription())));
    }

    @Test
    public void getOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(1, "ALL", 0, 10))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoList.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoList.get(0).getStatus().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoList.get(0).getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoList.get(0).getItem().getName())))
                .andExpect(jsonPath("$[0].item.description",
                        is(bookingDtoList.get(0).getItem().getDescription())));
    }

}