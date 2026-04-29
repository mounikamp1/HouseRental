package com.airbnb.booking.dto;

import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO - What we send back to clients
 */
public record BookingResponse(
    String id,
    String userId,
    String homeId,
    LocalDate checkIn,
    LocalDate checkOut,
    Integer guests,
    Long numberOfNights,
    Double pricePerNight,
    Double totalPrice,
    BookingStatus status,
    String guestName,
    String guestEmail,
    String guestPhone,
    String specialRequests,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    // Factory method to convert from Entity
    public static BookingResponse fromEntity(Booking booking) {
        return new BookingResponse(
            booking.getId(),
            booking.getUserId(),
            booking.getHomeId(),
            booking.getCheckIn(),
            booking.getCheckOut(),
            booking.getGuests(),
            booking.getNumberOfNights(),
            booking.getPricePerNight(),
            booking.getTotalPrice(),
            booking.getStatus(),
            booking.getGuestName(),
            booking.getGuestEmail(),
            booking.getGuestPhone(),
            booking.getSpecialRequests(),
            booking.getCreatedAt(),
            booking.getUpdatedAt()
        );
    }
}