package com.airbnb.booking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Java 21 Record for Booking Request
 * Immutable, concise, and modern
 */
public record BookingRequest(
    
    @NotBlank(message = "User ID is required")
    String userId,
    
    @NotBlank(message = "Home ID is required")
    String homeId,
    
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    LocalDate checkIn,
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    LocalDate checkOut,
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    Integer guests,
    
    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    Double pricePerNight,
    
    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String guestName,
    
    @NotBlank(message = "Guest email is required")
    @Email(message = "Valid email is required")
    String guestEmail,
    
    @Pattern(regexp = "^[0-9\\-\\+\\s\\(\\)]*$", message = "Invalid phone number format")
    String guestPhone,
    
    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    String specialRequests
) {
    
    // Compact constructor with validation
    public BookingRequest {
        if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}