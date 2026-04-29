package com.airbnb.booking.mapper;

import com.airbnb.booking.dto.BookingRequest;
import com.airbnb.booking.dto.BookingResponse;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Booking entity and DTOs.
 *
 * <p>Centralises all entity↔DTO transformations so controllers and services
 * never need to know the internal structure of each layer.
 */
@Component
public class BookingMapper {

    /**
     * Convert a {@link BookingRequest} DTO into a new, unsaved {@link Booking} entity.
     */
    public Booking toEntity(BookingRequest request) {
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
        return booking;
    }

    /**
     * Convert a {@link Booking} entity into a {@link BookingResponse} DTO.
     */
    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.fromEntity(booking);
    }
}