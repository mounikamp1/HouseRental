package com.airbnb.booking.controller;

import com.airbnb.booking.dto.BookingRequest;
import com.airbnb.booking.exception.BookingNotFoundException;
import com.airbnb.booking.exception.GlobalExceptionHandler;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import com.airbnb.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(GlobalExceptionHandler.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;
    private LocalDate checkIn;
    private LocalDate checkOut;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        checkIn  = LocalDate.now().plusDays(2);
        checkOut = LocalDate.now().plusDays(7);
    }

    @Test
    void health_returnsUp() throws Exception {
        mockMvc.perform(get("/api/bookings/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Booking Microservice"));
    }

    @Test
    void createBooking_validRequest_returns201() throws Exception {
        BookingRequest request = buildRequest(checkIn, checkOut);
        Booking savedBooking = buildBooking("id-1", checkIn, checkOut);
        given(bookingService.createBooking(any())).willReturn(savedBooking);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getAllBookings_returnsList() throws Exception {
        given(bookingService.getAllBookings()).willReturn(List.of(buildBooking("id-1", checkIn, checkOut)));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("id-1"));
    }

    @Test
    void getBookingById_found_returns200() throws Exception {
        given(bookingService.getBookingById("id-1")).willReturn(buildBooking("id-1", checkIn, checkOut));

        mockMvc.perform(get("/api/bookings/id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    void getBookingById_notFound_returns404() throws Exception {
        given(bookingService.getBookingById("missing"))
                .willThrow(new BookingNotFoundException("Booking not found with id: missing"));

        mockMvc.perform(get("/api/bookings/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getBookingsByUser_returnsList() throws Exception {
        given(bookingService.getBookingsByUserId("user-1")).willReturn(List.of(buildBooking("id-1", checkIn, checkOut)));

        mockMvc.perform(get("/api/bookings/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("id-1"));
    }

    @Test
    void getBookingsByHome_returnsList() throws Exception {
        given(bookingService.getBookingsByHomeId("home-1")).willReturn(List.of(buildBooking("id-1", checkIn, checkOut)));

        mockMvc.perform(get("/api/bookings/home/home-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("id-1"));
    }

    @Test
    void checkAvailability_available_returnsAvailableTrue() throws Exception {
        given(bookingService.checkAvailability(any(), any(), any())).willReturn(true);
        String body = String.format("{\"homeId\":\"home-1\",\"checkIn\":\"%s\",\"checkOut\":\"%s\"}", checkIn, checkOut);

        mockMvc.perform(post("/api/bookings/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void checkAvailability_notAvailable_returnsAvailableFalse() throws Exception {
        given(bookingService.checkAvailability(any(), any(), any())).willReturn(false);
        String body = String.format("{\"homeId\":\"home-1\",\"checkIn\":\"%s\",\"checkOut\":\"%s\"}", checkIn, checkOut);

        mockMvc.perform(post("/api/bookings/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void confirmBooking_returns200() throws Exception {
        Booking confirmed = buildBooking("id-1", checkIn, checkOut);
        confirmed.setStatus(BookingStatus.CONFIRMED);
        given(bookingService.confirmBooking("id-1")).willReturn(confirmed);

        mockMvc.perform(patch("/api/bookings/id-1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void cancelBooking_returns200() throws Exception {
        Booking cancelled = buildBooking("id-1", checkIn, checkOut);
        cancelled.setStatus(BookingStatus.CANCELLED);
        given(bookingService.cancelBooking("id-1")).willReturn(cancelled);

        mockMvc.perform(patch("/api/bookings/id-1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void deleteBooking_returns200WithMessage() throws Exception {
        willDoNothing().given(bookingService).deleteBooking("id-1");

        mockMvc.perform(delete("/api/bookings/id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking deleted successfully"))
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    void getTodayCheckIns_returnsList() throws Exception {
        given(bookingService.getTodayCheckIns()).willReturn(List.of(buildBooking("id-1", checkIn, checkOut)));

        mockMvc.perform(get("/api/bookings/checkins/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private static BookingRequest buildRequest(LocalDate checkIn, LocalDate checkOut) {
        return new BookingRequest("user-1","home-1",checkIn,checkOut,2,150.0,"John Doe","john@example.com","555-1234","No special requests");
    }

    private static Booking buildBooking(String id, LocalDate checkIn, LocalDate checkOut) {
        Booking b = new Booking();
        b.setId(id);
        b.setUserId("user-1");
        b.setHomeId("home-1");
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setGuests(2);
        b.setPricePerNight(150.0);
        b.setGuestName("John Doe");
        b.setGuestEmail("john@example.com");
        b.setGuestPhone("555-1234");
        b.setStatus(BookingStatus.PENDING);
        b.calculateNumberOfNights();
        b.calculateTotalPrice();
        return b;
    }
}