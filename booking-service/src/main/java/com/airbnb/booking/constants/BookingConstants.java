package com.airbnb.booking.constants;

/**
 * Shared constants for the Booking Service.
 */
public final class BookingConstants {

    private BookingConstants() {}

    /** Maximum allowed booking duration in nights. */
    public static final int MAX_BOOKING_NIGHTS = 365;

    /** Minimum guests per booking. */
    public static final int MIN_GUESTS = 1;

    /** Maximum guests per booking. */
    public static final int MAX_GUESTS = 20;

    /** Timeout in milliseconds for remote service calls. */
    public static final int SERVICE_TIMEOUT_MS = 10_000;

    /** Maximum price per night (sanity check). */
    public static final double MAX_PRICE_PER_NIGHT = 100_000.0;
}