package com.airbnb.booking.exception;

/**
 * Exception thrown when booking dates conflict with existing bookings
 */
public class BookingConflictException extends RuntimeException {
    
    public BookingConflictException(String message) {
        super(message);
    }
    
    public BookingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}