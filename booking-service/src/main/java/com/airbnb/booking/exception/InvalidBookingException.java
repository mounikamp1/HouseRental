package com.airbnb.booking.exception;

/**
 * Exception thrown when booking data is invalid
 */
public class InvalidBookingException extends RuntimeException {
    
    public InvalidBookingException(String message) {
        super(message);
    }
    
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}