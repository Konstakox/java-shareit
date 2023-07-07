package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoGiven;
import ru.practicum.shareit.booking.dto.BookingDtoIncoming;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constantsShareitServer.Constants.USER_ID;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Integer userId = 1;
    private final Integer itemId = 1;
    private final Integer bookingId = 1;

    private final BookingDtoIncoming bookingDtoIncoming = BookingDtoIncoming.builder()
            .start(LocalDateTime.of(3000, Month.JANUARY, 1, 0, 0))
            .end(LocalDateTime.of(4000, Month.JANUARY, 1, 0, 0))
            .itemId(itemId)
            .build();

    private final BookingDtoGiven bookingDtoGiven = BookingDtoGiven.builder()
            .id(bookingId)
            .start(LocalDateTime.of(3000, Month.JANUARY, 1, 0, 0))
            .end(LocalDateTime.of(4000, Month.JANUARY, 1, 0, 0))
            .status(StatusBooking.WAITING)
            .build();

    @SneakyThrows
    @Test
    void addBooking_thenResponseBookingDto() {
        when(bookingService.addBooking(userId, bookingDtoIncoming)).thenReturn(bookingDtoGiven);

        mockMvc.perform(post("/bookings").header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(bookingDtoIncoming)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoGiven)));

        verify(bookingService, times(1)).addBooking(userId, bookingDtoIncoming);
    }

    @SneakyThrows
    @Test
    void addBooking_thenResponseBookingDtoSimilarIsStudy() {
        when(bookingService.addBooking(userId, bookingDtoIncoming)).thenReturn(bookingDtoGiven);

        String result = mockMvc.perform(post("/bookings").header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(bookingDtoIncoming)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoGiven), result);
    }

    @SneakyThrows
    @Test
    void approvOrRejectBooking_statusApproved_thenResponseBookingDto() {
        when(bookingService.approvOrRejectBooking(anyInt(), anyInt(), eq("true"))).thenReturn(bookingDtoGiven);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID, 1)
                        .queryParam("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoGiven)));

        verify(bookingService, times(1)).approvOrRejectBooking(anyInt(), anyInt(), eq("true"));
    }

    @SneakyThrows
    @Test
    void getBookingOnlyBookerOrOwner_thenResponseBookingDto() {
        when(bookingService.getBookingOnlyBookerOrOwner(anyInt(), anyInt())).thenReturn(bookingDtoGiven);

        mockMvc.perform(get(String.format("/bookings/%d", bookingId)).header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoGiven)));

        verify(bookingService, times(1)).getBookingOnlyBookerOrOwner(anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllBookingsUser_thenResponseBookingDtoCollection() {
        List<BookingDtoGiven> bookingDtoGivenList = List.of(bookingDtoGiven);

        when(bookingService.getAllBookingsUser(anyInt(), any(), any(), any())).thenReturn(bookingDtoGivenList);

        mockMvc.perform(get("/bookings").header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoGivenList)));

        verify(bookingService, times(1)).getAllBookingsUser(anyInt(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllBookingsItemsUser_thenResponseBookingDtoCollection() {
        List<BookingDtoGiven> bookingDtoGivenList = List.of(bookingDtoGiven);

        when(bookingService.getAllBookingsItemsUser(anyInt(), any(), any(), any())).thenReturn(bookingDtoGivenList);

        mockMvc.perform(get("/bookings/owner").header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoGivenList)));

        verify(bookingService, times(1)).getAllBookingsItemsUser(anyInt(), any(), any(), any());
    }
}