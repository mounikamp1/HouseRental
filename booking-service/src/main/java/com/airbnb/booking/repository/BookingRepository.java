package com.airbnb.booking.repository;

import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Booking entity
 * Uses Spring Data MongoDB for automatic query generation
 */
@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    
    // Find all bookings for a specific user
    List<Booking> findByUserId(String userId);
    
    // Find all bookings for a specific home
    List<Booking> findByHomeId(String homeId);
    
    // Find bookings by status
    List<Booking> findByStatus(BookingStatus status);
    
    // Find user's bookings by status
    List<Booking> findByUserIdAndStatus(String userId, BookingStatus status);
    
    // Find bookings for a home within a date range
    @Query("{ 'homeId': ?0, 'status': { $in: ['PENDING', 'CONFIRMED'] }, " +
           "'checkIn': { $lte: ?2 }, 'checkOut': { $gt: ?1 } }")
    List<Booking> findOverlappingBookings(String homeId, LocalDate checkIn, LocalDate checkOut);
    
    // Advanced conflict detection query
    // Finds bookings that overlap with the given date range
    @Query("{ 'homeId': ?0, " +
           "'status': { $in: ['PENDING', 'CONFIRMED'] }, " +
           "$or: [ " +
           "  { 'checkIn': { $lte: ?2 }, 'checkOut': { $gt: ?1 } }, " +
           "  { 'checkIn': { $gte: ?1, $lt: ?2 } } " +
           "] }")
    List<Booking> findConflictingBookings(String homeId, LocalDate checkIn, LocalDate checkOut);
    
    // Count active bookings for a home
    @Query(value = "{ 'homeId': ?0, 'status': { $in: ['PENDING', 'CONFIRMED'] } }", count = true)
    long countActiveBookingsByHomeId(String homeId);
    
    // Find bookings checking in today
    @Query("{ 'checkIn': ?0, 'status': 'CONFIRMED' }")
    List<Booking> findCheckingInToday(LocalDate today);
    
    // Find bookings checking out today
    @Query("{ 'checkOut': ?0, 'status': 'CONFIRMED' }")
    List<Booking> findCheckingOutToday(LocalDate today);
    
    // Find upcoming bookings for a user
    @Query("{ 'userId': ?0, 'checkIn': { $gte: ?1 }, 'status': { $in: ['PENDING', 'CONFIRMED'] } }")
    List<Booking> findUpcomingBookingsByUserId(String userId, LocalDate fromDate);
    
    // Find past bookings for a user
    @Query("{ 'userId': ?0, 'checkOut': { $lt: ?1 } }")
    List<Booking> findPastBookingsByUserId(String userId, LocalDate beforeDate);
}