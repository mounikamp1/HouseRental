package com.airbnb.booking.controller;

import com.airbnb.booking.dto.AvailabilityRequest;
import com.airbnb.booking.dto.BookingRequest;
import com.airbnb.booking.dto.BookingResponse;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import com.airbnb.booking.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API Controller for Booking Operations
 * 
 * Endpoints:
 * - POST   /api/bookings              - Create booking
 * - GET    /api/bookings              - Get all bookings
 * - GET    /api/bookings/{id}         - Get booking by ID
 * - GET    /api/bookings/user/{id}    - Get user's bookings
 * - GET    /api/bookings/home/{id}    - Get home's bookings
 * - POST   /api/bookings/availability - Check availability
 * - PATCH  /api/bookings/{id}/status  - Update status
 * - PATCH  /api/bookings/{id}/confirm - Confirm booking
 * - PATCH  /api/bookings/{id}/cancel  - Cancel booking
 * - DELETE /api/bookings/{id}         - Delete booking
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:5000", "http://localhost:3000"})
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Booking Microservice");
        response.put("timestamp", System.currentTimeMillis());
        response.put("virtualThreads", "ENABLED");
        return ResponseEntity.ok(response);
    }
    
    /**
     * CREATE - New booking
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        logger.info("REST: Creating booking for home: {} by user: {}", request.homeId(), request.userId());
        
        Booking booking = bookingService.createBooking(request);
        BookingResponse response = BookingResponse.fromEntity(booking);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * READ - Get all bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        logger.info("REST: Fetching all bookings");
        
        List<BookingResponse> bookings = bookingService.getAllBookings()
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * READ - Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable String id) {
        logger.info("REST: Fetching booking by id: {}", id);
        
        Booking booking = bookingService.getBookingById(id);
        BookingResponse response = BookingResponse.fromEntity(booking);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * READ - Get bookings by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(@PathVariable String userId) {
        logger.info("REST: Fetching bookings for user: {}", userId);
        
        List<BookingResponse> bookings = bookingService.getBookingsByUserId(userId)
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * READ - Get upcoming bookings by user ID
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<BookingResponse>> getUpcomingBookingsByUser(@PathVariable String userId) {
        logger.info("REST: Fetching upcoming bookings for user: {}", userId);
        
        List<BookingResponse> bookings = bookingService.getUpcomingBookingsByUserId(userId)
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * READ - Get past bookings by user ID
     */
    @GetMapping("/user/{userId}/past")
    public ResponseEntity<List<BookingResponse>> getPastBookingsByUser(@PathVariable String userId) {
        logger.info("REST: Fetching past bookings for user: {}", userId);
        
        List<BookingResponse> bookings = bookingService.getPastBookingsByUserId(userId)
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * READ - Get bookings by home ID
     */
    @GetMapping("/home/{homeId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByHome(@PathVariable String homeId) {
        logger.info("REST: Fetching bookings for home: {}", homeId);
        
        List<BookingResponse> bookings = bookingService.getBookingsByHomeId(homeId)
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * CHECK - Availability (POST with body)
     */
    @PostMapping("/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(@Valid @RequestBody AvailabilityRequest request) {
        logger.info("REST: Checking availability for home: {} from {} to {}", 
                   request.homeId(), request.checkIn(), request.checkOut());
        
        boolean available = bookingService.checkAvailability(
            request.homeId(), 
            request.checkIn(), 
            request.checkOut()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("homeId", request.homeId());
        response.put("checkIn", request.checkIn());
        response.put("checkOut", request.checkOut());
        response.put("message", available ? "Property is available" : "Property is not available");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * CHECK - Availability (GET with query params - for easy testing)
     */
    @GetMapping("/availability/{homeId}")
    public ResponseEntity<Map<String, Object>> checkAvailabilitySimple(
            @PathVariable String homeId,
            @RequestParam String checkIn,
            @RequestParam String checkOut) {
        
        logger.info("REST: Checking availability for home: {} from {} to {}", homeId, checkIn, checkOut);
        
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);
        
        boolean available = bookingService.checkAvailability(homeId, checkInDate, checkOutDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("homeId", homeId);
        response.put("checkIn", checkInDate);
        response.put("checkOut", checkOutDate);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * UPDATE - Update booking status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        
        logger.info("REST: Updating booking {} status to: {}", id, status);
        
        BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
        Booking booking = bookingService.updateBookingStatus(id, bookingStatus);
        BookingResponse response = BookingResponse.fromEntity(booking);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * UPDATE - Confirm booking
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable String id) {
        logger.info("REST: Confirming booking: {}", id);
        
        Booking booking = bookingService.confirmBooking(id);
        BookingResponse response = BookingResponse.fromEntity(booking);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * UPDATE - Cancel booking
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable String id) {
        logger.info("REST: Cancelling booking: {}", id);
        
        Booking booking = bookingService.cancelBooking(id);
        BookingResponse response = BookingResponse.fromEntity(booking);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE - Delete booking
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBooking(@PathVariable String id) {
        logger.info("REST: Deleting booking: {}", id);
        
        bookingService.deleteBooking(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking deleted successfully");
        response.put("id", id);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * UTILITY - Get today's check-ins
     */
    @GetMapping("/checkins/today")
    public ResponseEntity<List<BookingResponse>> getTodayCheckIns() {
        logger.info("REST: Fetching today's check-ins");
        
        List<BookingResponse> bookings = bookingService.getTodayCheckIns()
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * UTILITY - Get today's check-outs
     */
    @GetMapping("/checkouts/today")
    public ResponseEntity<List<BookingResponse>> getTodayCheckOuts() {
        logger.info("REST: Fetching today's check-outs");
        
        List<BookingResponse> bookings = bookingService.getTodayCheckOuts()
            .stream()
            .map(BookingResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }
}