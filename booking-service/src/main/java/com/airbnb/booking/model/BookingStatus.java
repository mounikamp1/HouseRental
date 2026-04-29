package com.airbnb.booking.model;

/**
 * Booking Status Lifecycle
 */
public enum BookingStatus {
    PENDING,      // Initial state - awaiting confirmation
    CONFIRMED,    // Payment received / booking confirmed
    CANCELLED,    // User cancelled
    COMPLETED,    // Guest checked out
    NO_SHOW       // Guest didn't show up
}