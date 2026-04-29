package com.airbnb.booking.service;

import com.airbnb.booking.dto.AvailabilityRequest;
import com.airbnb.booking.dto.BookingRequest;
import com.airbnb.booking.exception.BookingConflictException;
import com.airbnb.booking.exception.BookingNotFoundException;
import com.airbnb.booking.exception.InvalidBookingException;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import com.airbnb.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Booking Service - Core Business Logic
 * 
 * Features:
 * - High-performance booking creation with conflict detection
 * - Virtual Threads for concurrent availability checks
 * - Transaction management
 * - Comprehensive validation
 */
@Service
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private BookingRepository bookingRepository;
    
    /**
     * Create a new booking with conflict detection
     * Uses Virtual Threads for high-performance concurrent processing
     */
    @Transactional
    public Booking createBooking(BookingRequest request) {
        logger.debug("Creating booking for home: {} by user: {}", request.homeId(), request.userId());
        
        // Validate dates
        validateDates(request.checkIn(), request.checkOut());
        
        // Check for conflicts (this can handle high concurrency with Virtual Threads)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            request.homeId(), 
            request.checkIn(), 
            request.checkOut()
        );
        
        if (!conflicts.isEmpty()) {
            logger.warn("Booking conflict detected for home: {} on dates {}-{}", 
                       request.homeId(), request.checkIn(), request.checkOut());
            throw new BookingConflictException(
                String.format("Property is not available from %s to %s. Found %d conflicting booking(s).",
                             request.checkIn(), request.checkOut(), conflicts.size())
            );
        }
        
        // Create booking entity
        Booking booking = new Booking();
        booking.setUserId(request.userId());
        booking.setHomeId(request.homeId());
        booking.setCheckIn(request.checkIn());
        booking.setCheckOut(request.checkOut());
        booking.setGuests(request.guests());
        booking.setPricePerNight(request.pricePerNight());
        booking.setGuestName(request.guestName());
        booking.setGuestEmail(request.guestEmail());
        booking.setGuestPhone(request.guestPhone());
        booking.setSpecialRequests(request.specialRequests());
        booking.setStatus(BookingStatus.PENDING);
        
        // Calculate derived fields
        booking.calculateNumberOfNights();
        booking.calculateTotalPrice();
        
        // Save to database
        Booking savedBooking = bookingRepository.save(booking);
        
        logger.info("Booking created successfully: {} for {} nights, total: ${}", 
                   savedBooking.getId(), savedBooking.getNumberOfNights(), savedBooking.getTotalPrice());
        
        return savedBooking;
    }
    
    /**
     * Check availability with Virtual Threads for high concurrency
     */
    @Async
    public CompletableFuture<Boolean> checkAvailabilityAsync(AvailabilityRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            logger.debug("Checking availability for home: {} from {} to {}", 
                        request.homeId(), request.checkIn(), request.checkOut());
            
            validateDates(request.checkIn(), request.checkOut());
            
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.homeId(), 
                request.checkIn(), 
                request.checkOut()
            );
            
            boolean available = conflicts.isEmpty();
            logger.debug("Availability result for home {}: {}", request.homeId(), available);
            
            return available;
        });
    }
    
    /**
     * Synchronous availability check
     */
    public boolean checkAvailability(String homeId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        
        List<Booking> conflicts = bookingRepository.findConflictingBookings(homeId, checkIn, checkOut);
        return conflicts.isEmpty();
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        logger.debug("Fetching all bookings");
        return bookingRepository.findAll();
    }
    
    /**
     * Get booking by ID
     */
    public Booking getBookingById(String id) {
        logger.debug("Fetching booking by id: {}", id);
        return bookingRepository.findById(id)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
    }
    
    /**
     * Get all bookings for a user
     */
    public List<Booking> getBookingsByUserId(String userId) {
        logger.debug("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId);
    }
    
    /**
     * Get upcoming bookings for a user
     */
    public List<Booking> getUpcomingBookingsByUserId(String userId) {
        logger.debug("Fetching upcoming bookings for user: {}", userId);
        return bookingRepository.findUpcomingBookingsByUserId(userId, LocalDate.now());
    }
    
    /**
     * Get past bookings for a user
     */
    public List<Booking> getPastBookingsByUserId(String userId) {
        logger.debug("Fetching past bookings for user: {}", userId);
        return bookingRepository.findPastBookingsByUserId(userId, LocalDate.now());
    }
    
    /**
     * Get all bookings for a home
     */
    public List<Booking> getBookingsByHomeId(String homeId) {
        logger.debug("Fetching bookings for home: {}", homeId);
        return bookingRepository.findByHomeId(homeId);
    }
    
    /**
     * Update booking status
     */
    @Transactional
    public Booking updateBookingStatus(String id, BookingStatus status) {
        logger.debug("Updating booking {} status to: {}", id, status);
        
        Booking booking = getBookingById(id);
        booking.setStatus(status);
        
        Booking updated = bookingRepository.save(booking);
        logger.info("Booking {} status updated to: {}", id, status);
        
        return updated;
    }
    
    /**
     * Confirm booking
     */
    @Transactional
    public Booking confirmBooking(String id) {
        logger.info("Confirming booking: {}", id);
        return updateBookingStatus(id, BookingStatus.CONFIRMED);
    }
    
    /**
     * Cancel booking with validation
     */
    @Transactional
    public Booking cancelBooking(String id) {
        logger.info("Cancelling booking: {}", id);
        
        Booking booking = getBookingById(id);
        
        if (!booking.canBeCancelled()) {
            throw new InvalidBookingException(
                "Booking cannot be cancelled. Either it's already completed/cancelled or check-in date has passed."
            );
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
    
    /**
     * Delete booking (hard delete)
     */
    @Transactional
    public void deleteBooking(String id) {
        logger.info("Deleting booking: {}", id);
        
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking not found with id: " + id);
        }
        
        bookingRepository.deleteById(id);
        logger.info("Booking deleted: {}", id);
    }
    
    /**
     * Get bookings checking in today
     */
    public List<Booking> getTodayCheckIns() {
        return bookingRepository.findCheckingInToday(LocalDate.now());
    }
    
    /**
     * Get bookings checking out today
     */
    public List<Booking> getTodayCheckOuts() {
        return bookingRepository.findCheckingOutToday(LocalDate.now());
    }
    
    /**
     * Validate dates
     */
    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new InvalidBookingException("Check-in and check-out dates are required");
        }
        
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new InvalidBookingException(
                String.format("Check-out date (%s) must be after check-in date (%s)", 
                             checkOut, checkIn)
            );
        }
        
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidBookingException("Check-in date cannot be in the past");
        }
        
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > 365) {
            throw new InvalidBookingException("Booking duration cannot exceed 365 nights");
        }
    }
}