package com.airbnb.booking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO for checking property availability
 */
public record AvailabilityRequest(
    
    @NotBlank(message = "Home ID is required")
    String homeId,
    
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in must be today or future")
    LocalDate checkIn,
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out must be in the future")
    LocalDate checkOut
) {
    
    public AvailabilityRequest {
        if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
    }
}